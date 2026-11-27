package fr.dojo.tools;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;

public final class Dojo {
    static final ObjectMapper JSON = new ObjectMapper();

    private Dojo() {
    }

    public static void main(String[] args) {
        try {
            run(args);
        } catch (Exception exception) {
            System.err.println("Dojo : " + exception.getMessage());
            System.exit(1);
        }
    }

    private static void run(String[] args) throws Exception {
        var root = Path.of("").toRealPath();
        var runtime = root.resolve(".dojo-runtime");
        if (args.length == 1 && args[0].equals("hook")) {
            new Measurement(runtime).capture(readHookEvent()).ifPresent(System.out::println);
        } else if (args.length == 1 && args[0].equals("check-ex3")) {
            var evaluation = new Evaluation(root, runtime);
            var checks = evaluation.checkEx3();
            System.out.println(JSON.writerWithDefaultPrettyPrinter().writeValueAsString(checks));
            if (!"mesure".equals(checks.get("statut"))
                    || ((Number) checks.get("attendus")).intValue() != ((Number) checks.get("reussis")).intValue()) {
                System.err.println("Consultez le résultat et les journaux de .dojo-runtime pour le détail.");
                System.exit(1);
            }
        } else if ((args.length == 2 || args.length == 4) && args[0].equals("evaluate")
                && (args.length == 2 || args[2].equals("--observations"))) {
            var observations = args.length == 4 ? CaseObservations.read(Path.of(args[3])) : null;
            evaluate(root, runtime, Long.parseLong(args[1]), observations);
        } else if ((args.length == 3 || args.length == 4) && args[0].equals("prepare-copy")) {
            var jar = Path.of(Dojo.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            new ProjectCopies(root, jar).prepareCopy(args[1], args.length == 4 ? args[2] : "",
                    Path.of(args[args.length - 1]));
        } else if (args.length == 2 && args[0].equals("prepare-copies")) {
            var jar = Path.of(Dojo.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            new ProjectCopies(root, jar).prepare(Path.of(args[1]));
        } else {
            throw new IOException("Usage : hook | check-ex3"
                    + " | evaluate NUMERO [--observations FICHIER] | prepare-copy EXERCICE [VARIANTE] DOSSIER");
        }
    }

    private static void evaluate(Path root, Path runtime, long number, CaseObservations observations) throws Exception {
        Files.createDirectories(runtime);
        var lock = runtime.resolve("rapport-" + number + ".lock");
        try (var channel = FileChannel.open(lock, CREATE, WRITE);
             var ignored = channel.lock()) {
            var json = new Evaluation(root, runtime).evaluate(number);
            var report = (ObjectNode) JSON.readTree(json);
            if (observations != null || !report.has("cas_verifies")) {
                var cases = observations == null ? null : observations.cases();
                report.set("cas_verifies", JSON.valueToTree(cases));
                report.set("nombre_cas_verifies", JSON.valueToTree(cases == null ? null : cases.size()));
                json = JSON.writerWithDefaultPrettyPrinter().writeValueAsString(report);
                var temporary = Files.createTempFile(runtime, "rapport-", ".tmp");
                try {
                    Files.writeString(temporary, json + "\n");
                    Files.move(temporary, runtime.resolve("rapport-" + number + ".json"), ATOMIC_MOVE, REPLACE_EXISTING);
                } finally {
                    Files.deleteIfExists(temporary);
                }
            }
            System.out.println(json);
            var label = new Measurement(runtime).closedAttempt(number).label();
            System.err.println("Rapport : " + ReportExport.save(runtime, number, label));
        }
    }

    private static JsonNode readHookEvent() throws IOException {
        try {
            var event = JSON.readTree(System.in);
            if (!event.isObject()) {
                throw new IOException("Le hook attend un objet JSON.");
            }
            return event;
        } catch (JacksonException exception) {
            throw new IOException("Le hook a reçu un événement JSON invalide.");
        }
    }
}
