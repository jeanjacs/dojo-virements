package fr.dojo.tools;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

final class ReportExport {
    private ReportExport() {
    }

    static Path save(Path runtime, long number, String label) throws IOException {
        var directory = Path.of(System.getProperty("user.home"), "dojo", "rapport");
        Files.createDirectories(directory);
        var report = runtime.resolve("rapport-" + number + ".json");
        var receipt = runtime.resolve("rapport-" + number + ".export");
        if (Files.isRegularFile(receipt)) {
            var previous = Path.of(Files.readString(receipt));
            if (Files.isRegularFile(previous) && Files.mismatch(report, previous) == -1) {
                return previous;
            }
        }
        var stem = "rapport-" + label.replace(' ', '-');
        for (int index = 1; ; index++) {
            var suffix = index == 1 ? "" : "-" + index;
            var destination = directory.resolve(stem + suffix + ".json");
            try {
                Files.copy(report, destination);
            } catch (FileAlreadyExistsException exception) {
                continue;
            }
            Files.writeString(receipt, destination.toString());
            return destination;
        }
    }
}
