package com.cobolt.objects;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * File system operations and path handling
 */
public class FileUtils {

    /**
     * Read file contents as bytes
     */
    public static byte[] readBytes(Path path) throws IOException {
        return Files.readAllBytes(path);
    }

    /**
     * Read file contents as string
     */
    public static String readString(Path path) throws IOException {
        return Files.readString(path, StandardCharsets.UTF_8);
    }

    /**
     * Write bytes to file
     */
    public static void writeBytes(Path path, byte[] data) throws IOException {
        Files.createDirectories(path.getParent());
        Files.write(path, data);
    }

    /**
     * Write string to file
     */
    public static void writeString(Path path, String content) throws IOException {
        Files.createDirectories(path.getParent());
        Files.writeString(path, content, StandardCharsets.UTF_8);
    }

    /**
     * Check if file exists
     */
    public static boolean exists(Path path) {
        return Files.exists(path);
    }

    /**
     * Delete file or directory recursively
     */
    public static void deleteRecursively(Path path) throws IOException {
        if (!Files.exists(path)) {
            return;
        }

        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * List all files in directory recursively
     */
    public static List<Path> listFilesRecursively(Path directory) throws IOException {
        if (!Files.exists(directory) || !Files.isDirectory(directory)) {
            return new ArrayList<>();
        }

        try (Stream<Path> stream = Files.walk(directory)) {
            return stream
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());
        }
    }

    /**
     * Get relative path from base to target
     */
    public static Path relativize(Path base, Path target) {
        return base.relativize(target);
    }

    /**
     * Create directory if it doesn't exist
     */
    public static void createDirectories(Path path) throws IOException {
        Files.createDirectories(path);
    }

    /**
     * Copy file
     */
    public static void copy(Path source, Path target) throws IOException {
        Files.createDirectories(target.getParent());
        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
    }
}
