package fr.dojo.tools;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FixtureReaderTest {
    private static final Path SAMPLE = Path.of("ex3", "FixtureReader.java").toAbsolutePath();
    private static final String FIXTURE = "fixtures/label.txt";
    private static final String EXPECTED = "fixture from base directory";

    @TempDir
    Path temporaryDirectory;

    @Test
    void readsTheFixtureWhenLaunchedFromTheBaseDirectory() throws Exception {
        var base = Files.createDirectory(temporaryDirectory.resolve("base directory"));
        writeFixture(base, EXPECTED);

        assertEquals(EXPECTED, readFixture(base, base));
    }

    @Test
    void readsTheSameFixtureWhenLaunchedFromAnotherDirectory() throws Exception {
        var base = Files.createDirectory(temporaryDirectory.resolve("base directory"));
        var workingDirectory = Files.createDirectory(temporaryDirectory.resolve("working directory"));
        writeFixture(base, EXPECTED);
        writeFixture(workingDirectory, "fixture from working directory");

        assertEquals(EXPECTED, readFixture(base, workingDirectory),
                "The fixture must come from the base directory passed to the program.");
    }

    private static void writeFixture(Path folder, String content) throws Exception {
        var file = folder.resolve(FIXTURE);
        Files.createDirectories(file.getParent());
        Files.writeString(file, content);
    }

    private static String readFixture(Path base, Path workingDirectory) throws Exception {
        var java = Path.of(System.getProperty("java.home"), "bin", "java").toString();
        var process = new ProcessBuilder(java, SAMPLE.toString(), base.toString(), FIXTURE)
                .directory(workingDirectory.toFile())
                .redirectErrorStream(true)
                .start();
        try {
            assertTrue(process.waitFor(15, TimeUnit.SECONDS), "FixtureReader did not terminate.");
            var output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            assertEquals(0, process.exitValue(), output);
            return output;
        } finally {
            if (process.isAlive()) {
                process.destroyForcibly();
            }
        }
    }
}
