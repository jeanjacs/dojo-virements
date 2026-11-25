package fr.dojo.tools;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

final class HookReference {
    private static final Set<String> FIELDS =
            Set.of("attempt_id", "timestamp", "tool_name", "session_id", "tool_use_id");
    private final Path root;

    HookReference(Path root) {
        this.root = root;
    }

    Map<String, ?> verify() throws IOException, InterruptedException {
        var work = Files.createTempDirectory("dojo-hook-");
        try {
            return verify(work.resolve("active"), work.resolve("events.jsonl"));
        } catch (JacksonException exception) {
            return Map.of("statut", "journal_invalide", "raison", "Une ligne du journal n'est pas un objet JSON valide.");
        } finally {
            ProjectCopies.deleteTemporaryTree(work);
        }
    }

    private Map<String, ?> verify(Path active, Path journal) throws IOException, InterruptedException {
        Files.writeString(active, "essai-a");
        var firstOk = emit(active, journal, "outil-a");
        var first = entries(journal);
        var replayOk = emit(active, journal, "outil-a");
        var replay = entries(journal);
        Files.delete(active);
        var beforeIdle = Files.exists(journal) ? Files.readAllBytes(journal) : new byte[0];
        var idleOk = emit(active, journal, "outil-b");
        var afterIdle = Files.exists(journal) ? Files.readAllBytes(journal) : new byte[0];
        Files.writeString(active, "essai-b");
        var secondOk = emit(active, journal, "outil-a");
        var entries = entries(journal);

        var checks = new LinkedHashMap<String, Boolean>();
        checks.put("collecte active", firstOk && first.size() == 1
                && first.getFirst().path("attempt_id").asString("").equals("essai-a"));
        checks.put("déduplication", replayOk && first.size() == 1 && replay.equals(first));
        checks.put("hors tentative", idleOk && Arrays.equals(beforeIdle, afterIdle));
        checks.put("tentatives distinctes", secondOk && entries.stream()
                .map(entry -> entry.path("attempt_id").asString("")).toList().equals(List.of("essai-a", "essai-b")));
        checks.put("champs autorisés et horodatage", !entries.isEmpty()
                && entries.stream().allMatch(HookReference::validEntry));
        return Map.of("statut", "mesure", "attendus", checks.size(), "controles", checks,
                "reussis", checks.values().stream().filter(Boolean::booleanValue).count(),
                "integration_Bob", "à vérifier pendant le dojo");
    }

    private boolean emit(Path active, Path journal, String toolId) throws IOException, InterruptedException {
        var java = Path.of(System.getProperty("java.home"), "bin", "java").toString();
        var process = new ProcessBuilder(java, "--class-path", System.getProperty("java.class.path"),
                root.resolve("dojo/ex3/Collector.java").toString(), "--active", active.toString(),
                "--journal", journal.toString())
                .redirectOutput(ProcessBuilder.Redirect.DISCARD).redirectError(ProcessBuilder.Redirect.DISCARD)
                .start();
        try {
            var event = Map.of("hook_event_name", "PreToolUse", "session_id", "bob-session",
                    "tool_name", "read_file", "tool_use_id", toolId,
                    "tool_input", Map.of("path", "fixture-private.txt"));
            try (var input = process.getOutputStream()) {
                Dojo.JSON.writeValue(input, event);
            }
            return process.waitFor(10, TimeUnit.SECONDS) && process.exitValue() == 0;
        } finally {
            if (process.isAlive()) {
                process.destroyForcibly();
            }
        }
    }

    private static List<JsonNode> entries(Path journal) throws IOException {
        var entries = new ArrayList<JsonNode>();
        if (Files.exists(journal)) {
            for (var line : Files.readAllLines(journal)) {
                var entry = Dojo.JSON.readTree(line);
                if (!entry.isObject()) {
                    throw new IOException("Une ligne du journal n'est pas un objet JSON.");
                }
                entries.add(entry);
            }
        }
        return entries;
    }

    private static boolean validEntry(JsonNode entry) {
        try {
            OffsetDateTime.parse(entry.path("timestamp").asString(""));
            return entry.propertyNames().equals(FIELDS)
                    && entry.path("tool_name").asString("").equals("read_file")
                    && entry.path("session_id").asString("").equals("bob-session")
                    && entry.path("tool_use_id").asString("").equals("outil-a");
        } catch (DateTimeParseException exception) {
            return false;
        }
    }
}
