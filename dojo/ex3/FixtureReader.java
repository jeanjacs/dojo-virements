import java.nio.file.Files;
import java.nio.file.Path;

class FixtureReader {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            throw new IllegalArgumentException("Usage: FixtureReader BASE_DIRECTORY RELATIVE_FILE");
        }
        System.out.print(Files.readString(Path.of(args[1])));
    }
}
