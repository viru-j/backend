package com.example.assistant;

import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.TypeFilter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Generates simple JUnit test skeletons for a given Java class using Spoon.
 */
public class JUnitTestGenerator {
    /**
     * Generates a JUnit test class for the given Java source file and writes it
     * to the project's src/test/java directory.
     *
     * @param classFile path to the Java class source file
     * @param projectRoot root directory of the Maven project
     */
    public void generateTests(Path classFile, Path projectRoot) throws IOException {
        Launcher launcher = new Launcher();
        launcher.addInputResource(classFile.toString());
        launcher.buildModel();
        CtModel model = launcher.getModel();

        CtClass<?> clazz = model.getElements(new TypeFilter<>(CtClass.class)).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No class found"));

        String packageName = clazz.getPackage() != null ? clazz.getPackage().getQualifiedName() : "";
        List<String> methodNames = clazz.getMethods().stream()
                .filter(m -> m.isPublic())
                .map(CtMethod::getSimpleName)
                .collect(Collectors.toList());

        String testClassName = clazz.getSimpleName() + "Test";
        StringBuilder sb = new StringBuilder();
        if (!packageName.isEmpty()) {
            sb.append("package ").append(packageName).append(";\n\n");
        }
        sb.append("import org.junit.jupiter.api.Test;\n\n");
        sb.append("public class ").append(testClassName).append(" {\n");
        for (String m : methodNames) {
            sb.append("    @Test\n");
            sb.append("    public void ").append(m).append("() {\n");
            sb.append("        // TODO: implement test\n");
            sb.append("    }\n\n");
        }
        sb.append("}\n");

        Path testRoot = projectRoot.resolve("src/test/java");
        if (!Files.exists(testRoot)) {
            Files.createDirectories(testRoot);
        }
        Path packagePath = testRoot.resolve(packageName.replace('.', '/'));
        Files.createDirectories(packagePath);
        Path output = packagePath.resolve(testClassName + ".java");
        Files.writeString(output, sb.toString());
    }
}
