package com.example.assistant;


import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtImport;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.TypeFilter;


import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**

 * Analyzes a Java source file using the Spoon framework.
 */
public class JavaFileAnalyzer {
    private CtModel model;


    /**
     * Parses the Java file from the given path.
     */

    public void parse(Path file) {
        Launcher launcher = new Launcher();
        launcher.addInputResource(file.toString());
        launcher.buildModel();
        model = launcher.getModel();

    }

    /**
     * Returns a list of method signatures in the parsed file.
     */
    public List<String> listMethodSignatures() {

        if (model == null) return List.of();
        return model.getElements(new TypeFilter<>(CtMethod.class)).stream()
                .map(CtMethod::getSignature)

                .collect(Collectors.toList());
    }

    /**
     * Returns a list of imports in the parsed file.
     */
    public List<String> listImports() {

        if (model == null) return List.of();
        return model.getElements(new TypeFilter<>(CtImport.class)).stream()
                .map(Object::toString)

                .collect(Collectors.toList());
    }

    /**
     * Extracts the source text of a specific method by name.
     */
    public String extractMethod(String methodName) {

        if (model == null) return "";
        return model.getElements(new TypeFilter<>(CtMethod.class)).stream()
                .filter(m -> m.getSimpleName().equals(methodName))
                .findFirst()
                .map(Object::toString)

                .orElse("");
    }
}
