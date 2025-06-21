package com.example.assistant;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

/**
 * Utility class that scans a project directory and stores a textual tree structure.
 */
public class ProjectStructureAnalyzer {
    private String lastScan;

    /**
     * Scans the given project directory and saves the textual tree to a file.
     */
    public void scan(String projectDir, Path output) throws IOException {
        Path root = Paths.get(projectDir);
        StringBuilder sb = new StringBuilder();
        Files.walk(root).forEach(p -> {
            String indent = "  ".repeat(root.relativize(p).getNameCount());
            sb.append(indent).append(p.getFileName()).append('\n');
        });
        lastScan = sb.toString();
        Files.writeString(output, lastScan);
    }

    public String getLastScan() {
        return lastScan;
    }
}
