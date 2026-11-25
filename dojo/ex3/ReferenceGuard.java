package fr.dojo.tools;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

public final class ReferenceGuard {
    private final Path root;

    ReferenceGuard(Path root) {
        this.root = root;
    }

    Optional<String> denial(JsonNode event) {
        // TODO: Decide whether this tool event targets a protected reference test.
        return Optional.empty();
    }

    public static void main(String[] args) throws IOException {
        var json = new ObjectMapper();
        var event = json.readTree(System.in);
        new ReferenceGuard(Path.of("").toRealPath()).denial(event).ifPresent(reason ->
                System.out.println(json.writeValueAsString(Map.of("hookSpecificOutput", Map.of(
                        "hookEventName", "PreToolUse", "permissionDecision", "deny", "permissionDecisionReason", reason)))));
    }
}
