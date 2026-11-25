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

import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;

final class Evaluation {
    private static final Map<String, ReferenceSuite> SUITES = Map.of(
            "verification", new ReferenceSuite("DojoSmokeTest", 1, "dojo/checks/ex1"),
            "ex1", new ReferenceSuite("ErrorContractTest", 3, "dojo/checks/ex1"),
            "ex2", new ReferenceSuite("TransferLookupTest", 2, "dojo/checks/ex2"),
            "ex3", new ReferenceSuite("FixtureReaderTest", 2, "dojo/checks/ex3", "dojo", "fr.dojo.tools"));
    private static final ReferenceSuite EX3_HOOK =
            new ReferenceSuite("ReferenceGuardTest", 6, "dojo/checks/ex3-hook", "dojo", "fr.dojo.tools");

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
            checks = exercise.equals("ex3") && !Files.exists(root.resolve("dojo/checks/ex3"))
                    && Files.isRegularFile(root.resolve("dojo/ex3/Collector.java"))
                    ? new HookReference(root).verify()
                    : runSuite(SUITES.get(exercise), log);
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
        return runSuite(SUITES.get("ex3"), runtime.resolve("ex3-check.log"));
    }

    Map<String, ?> checkEx3Hook() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        Files.createDirectories(runtime);
        return runSuite(EX3_HOOK, runtime.resolve("ex3-hook-check.log"));
    }

    private Map<String, ?> runSuite(ReferenceSuite suite, Path log)
            throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        var work = Files.createTempDirectory("dojo-evaluation-");
        try {
            prepareSuite(work, suite);
            var command = new ArrayList<String>();
            if (System.getProperty("os.name").toLowerCase(Locale.ROOT).startsWith("windows")) {
                command.addAll(List.of("cmd", "/c", "mvnw.cmd"));
            } else {
                command.add("./mvnw");
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

    private void prepareSuite(Path work, ReferenceSuite suite) throws IOException {
        for (var file : List.of("pom.xml", "mvnw", "mvnw.cmd")) {
            Files.copy(root.resolve(file), work.resolve(file), COPY_ATTRIBUTES);
        }
        for (var module : List.of(".mvn", "domaine", "application", "infra-jpa", "exposition")) {
            ProjectCopies.copyTree(root.resolve(module), work.resolve(module));
        }
        Files.createDirectory(work.resolve("dojo"));
        Files.copy(root.resolve("dojo/pom.xml"), work.resolve("dojo/pom.xml"));
        if (suite == EX3_HOOK) {
            var hookSources = Files.createDirectories(work.resolve("dojo/src/main/java/fr/dojo/tools"));
            Files.copy(root.resolve("dojo/ex3/ReferenceGuard.java"), hookSources.resolve("ReferenceGuard.java"));
        } else if (suite.module().equals("dojo")) {
            var sample = Path.of("dojo", "ex3", "FixtureReader.java");
            Files.createDirectories(work.resolve(sample.getParent()));
            Files.copy(root.resolve(sample), work.resolve(sample));
        }
        var testDirectory = work.resolve(suite.module()).resolve("src/test/java")
                .resolve(suite.testPackage().replace('.', '/'));
        ProjectCopies.copyTree(root.resolve(suite.sourceDirectory()), testDirectory);
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
        for (int index = 0; index < cases.getLength(); index++) {
            var test = (Element) cases.item(index);
            var status = List.of("failure", "error", "skipped").stream()
                    .filter(tag -> test.getElementsByTagName(tag).getLength() > 0)
                    .findFirst().orElse("ok");
            checks.add(Map.of("nom", test.getAttribute("name"), "statut", status));
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
