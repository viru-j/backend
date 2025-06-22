package com.example.assistant;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CodeSearcherTest {
    @Test
    public void searchFindsMatchingFiles() throws IOException {
        Path dir = Files.createTempDirectory("search");
        Path file1 = dir.resolve("A.java");
        Path file2 = dir.resolve("B.java");
        Files.writeString(file1, "class A { void foo(){} }");
        Files.writeString(file2, "class B {}");

        CodeSearcher searcher = new CodeSearcher();
        List<Path> results = searcher.search(dir, "foo");
        assertEquals(1, results.size());
        assertEquals(file1, results.get(0));
    }
}
