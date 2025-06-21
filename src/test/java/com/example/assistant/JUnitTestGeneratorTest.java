package com.example.assistant;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class JUnitTestGeneratorTest {
    @Test
    public void generateTestCreatesFile() throws IOException {
        Path tempDir = Files.createTempDirectory("proj");
        Path src = tempDir.resolve("src/main/java/com/example");
        Files.createDirectories(src);
        Path classFile = src.resolve("Foo.java");
        Files.writeString(classFile, "package com.example; public class Foo { public void bar(){} }");

        JUnitTestGenerator gen = new JUnitTestGenerator();
        gen.generateTests(classFile, tempDir);

        Path testFile = tempDir.resolve("src/test/java/com/example/FooTest.java");
        assertTrue(Files.exists(testFile));
        String content = Files.readString(testFile);
        assertTrue(content.contains("class FooTest"));
    }
}
