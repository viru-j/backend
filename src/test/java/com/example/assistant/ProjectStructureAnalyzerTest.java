package com.example.assistant;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class ProjectStructureAnalyzerTest {
    @Test
    public void scanCreatesFile() throws IOException {
        ProjectStructureAnalyzer analyzer = new ProjectStructureAnalyzer();
        Path tempDir = Files.createTempDirectory("proj");
        Files.createDirectories(tempDir.resolve("src/main/java"));
        Path output = tempDir.resolve("structure.txt");

        analyzer.scan(tempDir.toString(), output);
        assertTrue(Files.exists(output), "output file should exist");
        String content = Files.readString(output);
        assertTrue(content.contains("src"));
    }
}
