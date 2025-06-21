package com.example.assistant;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.imports.ImportDeclaration;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Analyzes a Java source file to extract method and import information.
 */
public class JavaFileAnalyzer {
    private CompilationUnit cu;

    /**
     * Parses the Java file from the given path.
     */
    public void parse(Path file) throws IOException {
        cu = StaticJavaParser.parse(file);
    }

    /**
     * Returns a list of method signatures in the parsed file.
     */
    public List<String> listMethodSignatures() {
        if (cu == null) return List.of();
        return cu.findAll(MethodDeclaration.class).stream()
                .map(MethodDeclaration::getDeclarationAsString)
                .collect(Collectors.toList());
    }

    /**
     * Returns a list of imports in the parsed file.
     */
    public List<String> listImports() {
        if (cu == null) return List.of();
        return cu.getImports().stream()
                .map(ImportDeclaration::getNameAsString)
                .collect(Collectors.toList());
    }

    /**
     * Extracts the source text of a specific method by name.
     */
    public String extractMethod(String methodName) {
        if (cu == null) return "";
        return cu.findAll(MethodDeclaration.class).stream()
                .filter(m -> m.getNameAsString().equals(methodName))
                .map(MethodDeclaration::toString)
                .findFirst()
                .orElse("");
    }
}
