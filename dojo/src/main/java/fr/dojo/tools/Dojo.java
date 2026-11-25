package fr.dojo.tools;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Path;

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
        } else if (args.length == 1 && (args[0].equals("check-ex3") || args[0].equals("check-ex3-hook"))) {
            var evaluation = new Evaluation(root, runtime);
            var checks = args[0].equals("check-ex3") ? evaluation.checkEx3() : evaluation.checkEx3Hook();
            System.out.println(JSON.writerWithDefaultPrettyPrinter().writeValueAsString(checks));
            if (!"mesure".equals(checks.get("statut"))
                    || ((Number) checks.get("attendus")).intValue() != ((Number) checks.get("reussis")).intValue()) {
                var log = args[0].equals("check-ex3") ? "ex3-check.log" : "ex3-hook-check.log";
                System.err.println("Consultez .dojo-runtime/" + log + " pour le détail.");
                System.exit(1);
            }
        } else if (args.length == 2 && args[0].equals("evaluate")) {
            System.out.println(new Evaluation(root, runtime).evaluate(Long.parseLong(args[1])));
        } else if (args.length == 2 && args[0].equals("prepare-copies")) {
            var jar = Path.of(Dojo.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            new ProjectCopies(root, jar).prepare(Path.of(args[1]));
        } else {
            throw new IOException("Usage : hook | check-ex3 | check-ex3-hook"
                    + " | evaluate NUMERO | prepare-copies DOSSIER");
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
