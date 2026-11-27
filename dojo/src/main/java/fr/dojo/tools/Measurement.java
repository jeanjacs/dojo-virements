package fr.dojo.tools;

import tools.jackson.databind.JsonNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

final class Measurement {
    private static final Pattern START = Pattern.compile(
            "\\[dojo (verification|ex[12] (?:avant|apres)|ex3(?: (?:avant|apres))?)\\]", Pattern.CASE_INSENSITIVE);
    private static final Pattern END = Pattern.compile("\\[dojo fin\\](?:\\s|$)", Pattern.CASE_INSENSITIVE);
    private final Path database;

    Measurement(Path runtime) {
        database = runtime.resolve("measure.sqlite3");
    }

    Optional<String> capture(JsonNode event) throws IOException, SQLException {
        var session = event.path("session_id").asString("");
        if (session.isBlank()) {
            return Optional.empty();
        }
        return switch (event.path("hook_event_name").asString("")) {
            case "UserPromptSubmit" -> capturePrompt(session, event.path("prompt").asString(""));
            case "PreToolUse" -> {
                captureTool(session, event.path("tool_use_id").asString(""), event.path("tool_name").asString(""));
                yield Optional.empty();
            }
            default -> Optional.empty();
        };
    }

    private Optional<String> capturePrompt(String session, String prompt) throws IOException, SQLException {
        var line = prompt.stripLeading().lines().findFirst().orElse("").stripTrailing();
        var start = START.matcher(line);
        if (start.matches()) {
            try (var connection = connect();
                 var insert = connection.prepareStatement(
                         "INSERT OR IGNORE INTO attempts(session, label, started) VALUES (?, ?, ?)")) {
                insert.setString(1, session);
                insert.setString(2, start.group(1).toLowerCase(java.util.Locale.ROOT));
                insert.setDouble(3, now());
                insert.executeUpdate();
            }
        } else if (END.matcher(line).lookingAt()) {
            return Optional.of(close(session));
        }
        return Optional.empty();
    }

    private String close(String session) throws IOException, SQLException {
        try (var connection = connect();
             var close = connection.prepareStatement(
                     "UPDATE attempts SET ended = ? WHERE session = ? AND ended IS NULL");
             var latest = connection.prepareStatement("""
                     SELECT id FROM attempts WHERE session = ? AND ended IS NOT NULL ORDER BY id DESC LIMIT 1
                     """)) {
            connection.setAutoCommit(false);
            close.setDouble(1, now());
            close.setString(2, session);
            close.executeUpdate();
            latest.setString(1, session);
            String result;
            try (var row = latest.executeQuery()) {
                result = row.next()
                        ? "Dojo : collecte close. Utilise le skill dojo-evaluation pour évaluer la tentative "
                          + row.getLong("id") + "."
                        : "Dojo : aucune tentative ouverte dans cette conversation.";
            }
            connection.commit();
            return result;
        }
    }

    private void captureTool(String session, String toolId, String name) throws IOException, SQLException {
        if (!Files.isRegularFile(database) || toolId.isBlank() || name.isBlank()) {
            return;
        }
        try (var connection = connect();
             var insert = connection.prepareStatement("""
                     INSERT OR IGNORE INTO calls
                     SELECT id, ?, ?, ? FROM attempts WHERE session = ? AND ended IS NULL
                     """)) {
            insert.setString(1, toolId);
            insert.setString(2, name);
            insert.setDouble(3, now());
            insert.setString(4, session);
            insert.executeUpdate();
        }
    }

    Attempt closedAttempt(long id) throws IOException, SQLException {
        try (var connection = connect();
             var attempt = connection.prepareStatement("""
                     SELECT label, started, ended FROM attempts WHERE id = ? AND ended IS NOT NULL
                     """);
             var counts = connection.prepareStatement("""
                     SELECT name, COUNT(*) AS total FROM calls WHERE attempt = ? GROUP BY name ORDER BY name
                     """)) {
            attempt.setLong(1, id);
            counts.setLong(1, id);
            try (var row = attempt.executeQuery()) {
                if (!row.next()) {
                    throw new IOException("Tentative clôturée introuvable.");
                }
                var calls = new LinkedHashMap<String, Integer>();
                try (var tools = counts.executeQuery()) {
                    while (tools.next()) {
                        calls.put(tools.getString("name"), tools.getInt("total"));
                    }
                }
                return new Attempt(row.getString("label"), row.getDouble("ended") - row.getDouble("started"), calls);
            }
        }
    }

    private Connection connect() throws IOException, SQLException {
        Files.createDirectories(database.getParent());
        var connection = DriverManager.getConnection("jdbc:sqlite:" + database);
        try (var sql = connection.createStatement()) {
            sql.execute("PRAGMA busy_timeout = 5000");
            sql.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS attempts (
                        id INTEGER PRIMARY KEY, session TEXT NOT NULL, label TEXT NOT NULL,
                        started REAL NOT NULL, ended REAL
                    )
                    """);
            sql.executeUpdate("""
                    CREATE UNIQUE INDEX IF NOT EXISTS one_open_per_session
                    ON attempts(session) WHERE ended IS NULL
                    """);
            sql.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS calls (
                        attempt INTEGER NOT NULL, tool_id TEXT NOT NULL, name TEXT NOT NULL,
                        observed REAL NOT NULL, PRIMARY KEY (attempt, tool_id)
                    )
                    """);
            return connection;
        } catch (SQLException exception) {
            connection.close();
            throw exception;
        }
    }

    private static double now() {
        return System.currentTimeMillis() / 1000.0;
    }

    record Attempt(String label, double duration, Map<String, Integer> calls) {
    }
}
