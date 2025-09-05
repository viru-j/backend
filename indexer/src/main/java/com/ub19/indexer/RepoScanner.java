package com.ub19.indexer;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * Scans a repository path and returns files matching supported extensions.
 */
public class RepoScanner {

    private static final String JAVA = ".java";
    private static final String MD = ".md";
    private static final String ADOC = ".adoc";

    public List<Path> scan(Path root) throws IOException {
        List<Path> results = new ArrayList<>();
        Files.walkFileTree(root, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                String name = file.getFileName().toString().toLowerCase();
                if (name.endsWith(JAVA) || name.endsWith(MD) || name.endsWith(ADOC)) {
                    results.add(file);
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return results;
    }
}
