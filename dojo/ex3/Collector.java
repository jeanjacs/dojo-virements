import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class Collector {
    public static void main(String[] args) throws IOException {
        if (args.length != 4 || !args[0].equals("--active") || !args[2].equals("--journal")) {
            throw new IllegalArgumentException("Usage: --active FILE --journal FILE");
        }
        var event = new ObjectMapper().readTree(System.in);
        var active = Path.of(args[1]);
        var attempt = Files.exists(active) ? Files.readString(active).strip() : "";
        collect(event, attempt, Path.of(args[3]));
    }

    private static void collect(JsonNode event, String attempt, Path journal) throws IOException {
        // Complete this method using the contract in docs/exercices/ex3.md.
    }
}
