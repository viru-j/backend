package com.example.assistant;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JavaFileAnalyzerTest {
    @Test
    public void listMethodsWorks() throws IOException {
        Path temp = Files.createTempFile("TestClass", ".java");
        Files.writeString(temp, "public class TestClass { void a(){} int b(){return 1;} }");
        JavaFileAnalyzer analyzer = new JavaFileAnalyzer();
        analyzer.parse(temp);
        List<String> methods = analyzer.listMethodSignatures();
        assertEquals(2, methods.size());
        assertTrue(methods.get(0).contains("void a()"));
        assertTrue(methods.get(1).contains("int b()"));
    }
}
