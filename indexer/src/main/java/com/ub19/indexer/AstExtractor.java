package com.ub19.indexer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Extracts method information from Java source files.
 */
public class AstExtractor {

    public List<MethodInfo> extract(Path file) throws IOException {
        CompilationUnit cu = StaticJavaParser.parse(file);
        String pkg = cu.getPackageDeclaration().map(pd -> pd.getName().toString()).orElse("");
        List<MethodInfo> methods = new ArrayList<>();
        for (ClassOrInterfaceDeclaration cls : cu.findAll(ClassOrInterfaceDeclaration.class)) {
            String classFqn = pkg.isEmpty() ? cls.getNameAsString() : pkg + "." + cls.getNameAsString();
            for (MethodDeclaration m : cls.getMethods()) {
                Optional<com.github.javaparser.Position> begin = m.getBegin();
                Optional<com.github.javaparser.Position> end = m.getEnd();
                int lineStart = begin.map(com.github.javaparser.Position::line).orElse(-1);
                int lineEnd = end.map(com.github.javaparser.Position::line).orElse(-1);
                String methodName = m.getNameAsString();
                String methodFqn = classFqn + "#" + methodName;
                List<String> throwsTypes = m.getThrownExceptions().stream()
                        .map(Object::toString)
                        .collect(Collectors.toList());
                List<String> annotations = m.getAnnotations().stream()
                        .map(a -> a.getName().getIdentifier())
                        .filter(n -> n.endsWith("Mapping"))
                        .collect(Collectors.toList());
                methods.add(new MethodInfo(classFqn, methodName, methodFqn, lineStart, lineEnd, throwsTypes, annotations));
            }
        }
        return methods;
    }

    public record MethodInfo(
            String classFqn,
            String methodName,
            String methodFqn,
            int lineStart,
            int lineEnd,
            List<String> throwsTypes,
            List<String> endpointAnnotations) { }
}
