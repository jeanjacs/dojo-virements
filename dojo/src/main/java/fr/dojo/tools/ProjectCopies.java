package fr.dojo.tools;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Set;

import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;

final class ProjectCopies {
    static final Set<String> VARIANTS = Set.of("avant", "apres");
    static final Set<String> EXERCISES = Set.of("verification", "ex1", "ex2", "ex3");
    private static final Set<String> EXCLUDED = Set.of(
            ".git", ".dojo-runtime", ".dojo-exercise", ".dojo-variant", "target", "__pycache__", ".idea", ".DS_Store",
            "Archive.zip", ".dojo-build-presentation", "deliverables");
    private static final List<String> COPIES = List.of(
            "verification", "ex1-avant", "ex1-apres", "ex2-avant", "ex2-apres", "ex3-avant", "ex3-apres");
    private static final Path ORGANIZER_FILES = Path.of("dojo", "organizer");
    private static final Path WORKSHOP_SUPPORTS = Path.of("docs", "supports");

    private final Path root;
    private final Path jar;

    ProjectCopies(Path root, Path jar) {
        this.root = root;
        this.jar = jar;
    }

    void prepare(Path destination) throws IOException {
        destination = resolveDestination(destination.toAbsolutePath().normalize());
        if (destination.startsWith(root)) {
            throw new IOException("Choisissez un dossier extérieur au dépôt.");
        }
        for (var name : COPIES) {
            if (Files.exists(destination.resolve(name), java.nio.file.LinkOption.NOFOLLOW_LINKS)) {
                throw new IOException("Le dossier " + name + " existe déjà.");
            }
        }
        Files.createDirectories(destination);
        for (var name : COPIES) {
            var parts = name.split("-", 2);
            prepareCopy(parts[0], parts.length == 2 ? parts[1] : "", destination.resolve(name));
        }
    }

    void prepareCopy(String exercise, String variant, Path destination) throws IOException {
        if (!EXERCISES.contains(exercise)) {
            throw new IOException("Exercice attendu : verification, ex1, ex2 ou ex3.");
        }
        if (!variant.isEmpty() && (exercise.equals("verification") || !VARIANTS.contains(variant))) {
            throw new IOException("Variante attendue : avant ou apres pour un exercice.");
        }
        destination = resolveDestination(destination.toAbsolutePath().normalize());
        if (destination.startsWith(root)) {
            throw new IOException("Choisissez un dossier extérieur au dépôt.");
        }
        Files.createDirectories(destination.getParent());
        Files.createDirectory(destination);
        try {
            copyTree(root, destination);
            var copyJar = destination.resolve("dojo/target/dojo-tools.jar");
            Files.createDirectories(copyJar.getParent());
            Files.copy(jar, copyJar);
        } catch (IOException exception) {
            try {
                deleteTemporaryTree(destination);
            } catch (IOException cleanup) {
                exception.addSuppressed(cleanup);
            }
            throw exception;
        }
    }

    private static Path resolveDestination(Path path) throws IOException {
        if (Files.exists(path, java.nio.file.LinkOption.NOFOLLOW_LINKS)) {
            return path.toRealPath();
        }
        return resolveDestination(path.getParent()).resolve(path.getFileName());
    }

    static void copyTree(Path source, Path target) throws IOException {
        Files.walkFileTree(source, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path folder, BasicFileAttributes attributes) throws IOException {
                var relative = source.relativize(folder);
                if (!folder.equals(source) && (EXCLUDED.contains(folder.getFileName().toString())
                        || relative.equals(ORGANIZER_FILES) || relative.equals(WORKSHOP_SUPPORTS))) {
                    return FileVisitResult.SKIP_SUBTREE;
                }
                Files.createDirectories(target.resolve(relative));
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
                var relative = source.relativize(file);
                if (attributes.isRegularFile() && !EXCLUDED.contains(file.getFileName().toString())) {
                    Files.copy(file, target.resolve(relative), COPY_ATTRIBUTES);
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    static void deleteTemporaryTree(Path directory) throws IOException {
        Files.walkFileTree(directory, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path folder, IOException error) throws IOException {
                if (error != null) {
                    throw error;
                }
                Files.delete(folder);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
