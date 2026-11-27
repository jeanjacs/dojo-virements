package fr.dojo.tools;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

final class Evaluation {
    private static final Map<String, ReferenceSuite> SUITES = Map.of(
            "verification", new ReferenceSuite("DojoSmokeTest", 1, "dojo/checks/ex1"),
            "ex1", new ReferenceSuite("ErrorContractTest", 3, "dojo/checks/ex1"),
            "ex2", new ReferenceSuite("TransferLookupTest", 2, "dojo/checks/ex2"));
    private static final ReferenceSuite EX3 = new ReferenceSuite("TransferLookupArchitectureTest", 4,
            null, "exposition", "fr.dojo.virements.exposition.architecture");
    private static final String LOOKUP_TEST = "applicationLooksUpTheTransferThroughTheOutputPort";

    private final Path root;
    private final Path runtime;

    Evaluation(Path root, Path runtime) {
        this.root = root;
        this.runtime = runtime;
    }

    String evaluate(long number) throws Exception {
        var attempt = new Measurement(runtime).closedAttempt(number);
        var reportPath = runtime.resolve("rapport-" + number + ".json");
        if (Files.isRegularFile(reportPath)) {
            return Files.readString(reportPath);
        }
        var exercise = attempt.label().split(" ", 2)[0];
        var log = runtime.resolve("evaluation-" + number + ".log");
        Map<String, ?> checks;
        try {
            checks = exercise.equals("ex3") ? checkEx3() : runSuite(SUITES.get(exercise), log);
        } catch (IOException | ParserConfigurationException | SAXException exception) {
            checks = Map.of("statut", "non_mesurable", "raison", exception.toString());
        }
        var report = new LinkedHashMap<String, Object>();
        report.put("id", number);
        report.put("tentative", attempt.label());
        report.put("duree_secondes", Math.round(attempt.duration() * 10) / 10.0);
        report.put("appels_observes", attempt.calls().values().stream().mapToInt(Integer::intValue).sum());
        report.put("appels_par_outil", attempt.calls());
        report.put("evaluation", checks);
        var json = Dojo.JSON.writerWithDefaultPrettyPrinter().writeValueAsString(report);
        Files.writeString(reportPath, json + "\n");
        return json;
    }

    Map<String, ?> checkEx3() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        Files.createDirectories(runtime);
        var testFile = root.resolve("exposition/src/test/java/fr/dojo/virements/exposition/architecture/"
                + "TransferLookupArchitectureTest.java");
        if (!Files.isRegularFile(testFile)) {
            return Map.of("statut", "non_mesurable", "attendus", 2,
                    "raison", "Test ArchUnit absent. Ouvrez la branche de l’exercice 3.");
        }
        var baseline = runSuite(EX3, runtime.resolve("ex3-check.log"));
        if (!"mesure".equals(baseline.get("statut"))
                || !(baseline.get("reussis") instanceof Number passed) || passed.intValue() != EX3.expected()) {
            return Map.of("statut", "non_mesurable", "attendus", 2,
                    "raison", "La suite ArchUnit doit réussir sur le code actuel. Voir .dojo-runtime/ex3-check.log.",
                    "suite_initiale", baseline);
        }

        var altered = runSuite(EX3, runtime.resolve("ex3-violation.log"), true);
        if (!"mesure".equals(altered.get("statut")) || !(altered.get("controles") instanceof List<?> controls)) {
            return Map.of("statut", "non_mesurable", "attendus", 2,
                    "raison", "Impossible de vérifier le cas sans appel au port. Voir .dojo-runtime/ex3-violation.log.",
                    "suite_avec_violation", altered);
        }
        boolean detected = controls.stream().anyMatch(control -> control instanceof Map<?, ?> test
                && LOOKUP_TEST.equals(test.get("nom")) && "failure".equals(test.get("statut")));
        return Map.of("statut", "mesure", "attendus", 2, "reussis", detected ? 2 : 1,
                "controles", List.of(
                        Map.of("nom", "Suite ArchUnit sur le code actuel", "statut", "ok"),
                        Map.of("nom", "Détection de l’absence d’appel à TransferRepository.find",
                                "statut", detected ? "ok" : "failure")),
                "diagnostic", detected ? "Le test détecte l’absence d’appel à TransferRepository.find."
                        : "Le test ne détecte pas l’absence d’appel à TransferRepository.find.");
    }

    private Map<String, ?> runSuite(ReferenceSuite suite, Path log)
            throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        return runSuite(suite, log, false);
    }

    private Map<String, ?> runSuite(ReferenceSuite suite, Path log, boolean withoutTransferLookup)
            throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        var work = Files.createTempDirectory("dojo-evaluation-");
        try {
            prepareSuite(work, suite);
            if (withoutTransferLookup) {
                removeTransferLookup(work);
            }
            var command = new ArrayList<String>();
            if (System.getProperty("os.name").toLowerCase(Locale.ROOT).startsWith("windows")) {
                command.addAll(List.of("cmd", "/c", "mvn.cmd"));
            } else {
                command.add("mvn");
            }
            command.addAll(List.of("-B", "-pl", suite.module(), "-am", "-Dsurefire.failIfNoSpecifiedTests=false",
                    "-Dtest=" + suite.className(), "test"));
            var process = new ProcessBuilder(command).directory(work.toFile())
                    .redirectErrorStream(true).redirectOutput(log.toFile()).start();
            try {
                if (!process.waitFor(180, TimeUnit.SECONDS)) {
                    throw new IOException("Délai Maven dépassé. Vérifier le journal Maven.");
                }
            } finally {
                if (process.isAlive()) {
                    process.descendants().forEach(ProcessHandle::destroyForcibly);
                    process.destroyForcibly();
                }
            }
            return readSuite(work, suite);
        } finally {
            ProjectCopies.deleteTemporaryTree(work);
        }
    }

    private static void removeTransferLookup(Path work) throws IOException {
        var service = work.resolve("application/src/main/java/fr/dojo/virements/application/transfer/TransferService.java");
        var code = Files.readString(service);
        var lookup = "return transfers.find(id).orElseThrow(() -> new TransferNotFoundException(id));";
        if (code.indexOf(lookup) == -1 || code.indexOf(lookup) != code.lastIndexOf(lookup)) {
            throw new IOException("Impossible d’isoler l’appel de consultation. Vérifier TransferService.getTransfer.");
        }
        Files.writeString(service, code.replace(lookup, "throw new TransferNotFoundException(id);"));
    }

    private void prepareSuite(Path work, ReferenceSuite suite) throws IOException {
        Files.copy(root.resolve("pom.xml"), work.resolve("pom.xml"));
        for (var module : List.of("domaine", "application", "infra-jpa", "exposition")) {
            ProjectCopies.copyTree(root.resolve(module), work.resolve(module));
        }
        Files.createDirectory(work.resolve("dojo"));
        Files.copy(root.resolve("dojo/pom.xml"), work.resolve("dojo/pom.xml"));
        var module = work.resolve(suite.module());
        var packageDirectory = suite.testPackage().replace('.', '/');
        if (suite.sourceDirectory() != null) {
            ProjectCopies.copyTree(root.resolve(suite.sourceDirectory()),
                    module.resolve("src/test/java").resolve(packageDirectory));
        }
    }

    private Map<String, ?> readSuite(Path work, ReferenceSuite suite)
            throws IOException, ParserConfigurationException, SAXException {
        var folder = work.resolve(suite.module()).resolve("target/surefire-reports");
        var report = folder.resolve("TEST-" + suite.testPackage() + "." + suite.className() + ".xml");
        if (!Files.isRegularFile(report)) {
            return Map.of("statut", "non_mesurable", "attendus", suite.expected(),
                    "raison", "Aucun rapport de test. Vérifier le journal Maven.");
        }
        var factory = DocumentBuilderFactory.newInstance();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        var cases = factory.newDocumentBuilder().parse(report.toFile()).getElementsByTagName("testcase");
        var checks = new ArrayList<Map<String, String>>();
        boolean portBlocked = false;
        for (int index = 0; index < cases.getLength(); index++) {
            var test = (Element) cases.item(index);
            var status = List.of("failure", "error", "skipped").stream()
                    .filter(tag -> test.getElementsByTagName(tag).getLength() > 0)
                    .findFirst().orElse("ok");
            checks.add(Map.of("nom", test.getAttribute("name"), "statut", status));
            if (status.equals("error")) {
                var trace = test.getElementsByTagName("error").item(0).getTextContent();
                portBlocked |= trace.contains("org.apache.tomcat.util.net.NioEndpoint.bind")
                        && (trace.contains("java.net.SocketException: Operation not permitted")
                        || trace.contains("java.net.SocketException: Permission denied"));
            }
        }
        if (portBlocked) {
            return Map.of("statut", "non_mesurable", "attendus", suite.expected(),
                    "raison", "Tomcat ne peut pas ouvrir le port HTTP de test. Vérifier les permissions du processus Maven.",
                    "controles", checks);
        }
        return Map.of("statut", checks.size() == suite.expected() ? "mesure" : "non_mesurable",
                "attendus", suite.expected(), "reussis", checks.stream().filter(c -> c.get("statut").equals("ok")).count(),
                "controles", checks);
    }

    private record ReferenceSuite(String className, int expected, String sourceDirectory,
                                  String module, String testPackage) {
        ReferenceSuite(String className, int expected, String sourceDirectory) {
            this(className, expected, sourceDirectory, "exposition", "fr.dojo.virements");
        }
    }
}
