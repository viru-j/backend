package com.example.assistant;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Utility to search for a keyword inside Java files under a directory.
 */
public class CodeSearcher {
    /**
     * Returns a list of Java file paths that contain the given keyword.
     * @param root the root directory to search
     * @param keyword the text to look for
     */
    public List<Path> search(Path root, String keyword) throws IOException {
        List<Path> results = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(root)) {
            paths.filter(p -> p.toString().endsWith(".java")).forEach(p -> {
                try {
                    String content = Files.readString(p);
                    if (content.contains(keyword)) {
                        results.add(p);
                    }
                } catch (IOException ignored) {
                }
            });
        }
        return results;
    }
}
