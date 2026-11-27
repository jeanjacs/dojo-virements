package fr.dojo.tools;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.DriverManager;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MeasurementTest {
    @TempDir
    Path runtime;

    @Test
    void capturesOnlyMetadataBetweenMarkersWithSessionIsolationAndDeduplication() throws Exception {
        var measurement = new Measurement(runtime);
        measurePrompt(measurement, "alice", "[dojo ex3] sur la même ligne");
        measureTool(measurement, "alice", "inactive");
        assertFalse(Files.exists(runtime.resolve("measure.sqlite3")));

        measurePrompt(measurement, "alice", "[dojo ex3]\nTravail");
        measurePrompt(measurement, "alice", "[dojo ex2 avant]\nNouvel envoi");
        measureTool(measurement, "alice", "tool-a");
        measureTool(measurement, "alice", "tool-a");
        measureTool(measurement, "bob", "tool-b");
        assertThrows(java.io.IOException.class, () -> measurement.closedAttempt(1));
        assertTrue(measurePrompt(measurement, "alice", "[dojo fin] Évalue cette tentative.").contains("tentative 1"));
        measureTool(measurement, "alice", "evaluation");

        measurePrompt(measurement, "alice", "[dojo ex3 apres]");
        measureTool(measurement, "alice", "tool-a");
        assertTrue(measurePrompt(measurement, "alice", "[dojo fin]").contains("tentative 2"));
        assertEquals("ex3", measurement.closedAttempt(1).label());
        assertEquals("ex3 apres", measurement.closedAttempt(2).label());
        assertEquals(Map.of("read_file", 1), measurement.closedAttempt(1).calls());
        assertEquals(Map.of("read_file", 1), measurement.closedAttempt(2).calls());
        assertFalse(new String(Files.readAllBytes(runtime.resolve("measure.sqlite3")),
                java.nio.charset.StandardCharsets.ISO_8859_1).contains("private-fixture"));
    }

    @Test
    void readsExistingSqliteAttemptsAndReports() throws Exception {
        try (var connection = DriverManager.getConnection("jdbc:sqlite:" + runtime.resolve("measure.sqlite3"));
             var statement = connection.createStatement()) {
            statement.executeUpdate("""
                    CREATE TABLE attempts (id INTEGER PRIMARY KEY, session TEXT NOT NULL, label TEXT NOT NULL,
                    started REAL NOT NULL, ended REAL)
                    """);
            statement.executeUpdate("INSERT INTO attempts VALUES (42, 'legacy', 'ex3 avant', 1200.5, 1203)");
            statement.executeUpdate("""
                    CREATE TABLE calls (attempt INTEGER NOT NULL, tool_id TEXT NOT NULL, name TEXT NOT NULL,
                    observed REAL NOT NULL, PRIMARY KEY (attempt, tool_id))
                    """);
            statement.executeUpdate("INSERT INTO calls VALUES (42, 'use-a', 'read_file', 1201)");
        }
        var previousReport = "{\"id\":42,\"appels_observes\":1}\n";
        Files.writeString(runtime.resolve("rapport-42.json"), previousReport);
        var attempt = new Measurement(runtime).closedAttempt(42);
        assertEquals(2.5, attempt.duration());
        assertEquals(Map.of("read_file", 1), attempt.calls());
        assertEquals(previousReport, new Evaluation(Path.of(""), runtime).evaluate(42));
    }

    private static String measurePrompt(Measurement measurement, String session, String prompt) throws Exception {
        return measurement.capture(Dojo.JSON.valueToTree(
                Map.of("hook_event_name", "UserPromptSubmit", "session_id", session, "prompt", prompt))).orElse("");
    }

    private static void measureTool(Measurement measurement, String session, String id) throws Exception {
        measurement.capture(Dojo.JSON.valueToTree(Map.of("hook_event_name", "PreToolUse",
                "session_id", session, "tool_use_id", id, "tool_name", "read_file",
                "tool_input", Map.of("path", "private-fixture"))));
    }
}
