package fr.dojo.tools;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import tools.jackson.databind.ObjectMapper;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ReferenceGuardTest {
    private static final ObjectMapper JSON = new ObjectMapper();
    @TempDir
    Path root;

    @ParameterizedTest
    @ValueSource(strings = {"write_file", "apply_diff", "insert_content", "search_and_replace"})
    void deniesEditsToReferenceTests(String tool) {
        var guard = new ReferenceGuard(root);
        assertTrue(guard.denial(event(tool, Map.of("path", "dojo/reference/SmokeTest.java"))).isPresent());
        assertTrue(guard.denial(event(tool, Map.of("path", "dojo/ex3/../checks/ex3/ExampleTest.java"))).isPresent());
    }

    @Test
    void deniesAnEditWhenOneOfSeveralTargetsIsProtected() {
        var guard = new ReferenceGuard(root);
        var input = Map.of("files", List.of(
                Map.of("path", "dojo/ex3/Exercise.java"),
                Map.of("path", root.resolve("dojo/checks/ex3/ExampleTest.java").toString())));

        assertTrue(guard.denial(event("apply_diff", input)).isPresent());
    }

    @Test
    void allowsReadingReferenceTestsAndEditingTheProgram() {
        var guard = new ReferenceGuard(root);
        assertTrue(guard.denial(event("read_file", Map.of("path", "dojo/checks/ex3/ExampleTest.java"))).isEmpty());
        assertTrue(guard.denial(event("apply_diff", Map.of("path", "dojo/ex3/Exercise.java"))).isEmpty());
    }

    private static tools.jackson.databind.JsonNode event(String tool, Map<String, ?> input) {
        return JSON.valueToTree(Map.of(
                "hook_event_name", "PreToolUse", "tool_name", tool, "tool_input", input));
    }
}
