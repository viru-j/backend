package com.example.assistant;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class CodeChunkerTest {
    @Test
    public void chunkMethodsExtractsMethods() throws IOException {
        Path file = Files.createTempFile("Test", ".java");
        Files.writeString(file, "public class Test { void a(){} int b(){return 1;} }");

        CodeChunker chunker = new CodeChunker();
        Map<String, String> chunks = chunker.chunkMethods(file);

        assertTrue(chunks.containsKey("a"));
        assertTrue(chunks.get("a").contains("void a()"));
        assertTrue(chunks.containsKey("b"));
    }
}
