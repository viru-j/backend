package com.example.assistant;

import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.TypeFilter;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Splits a Java source file into method chunks using Spoon.
 */
public class CodeChunker {
    /**
     * Returns a map from method name to the source code of that method.
     * @param javaFile the source file to parse
     */
    public Map<String, String> chunkMethods(Path javaFile) {
        Launcher launcher = new Launcher();
        launcher.addInputResource(javaFile.toString());
        launcher.buildModel();
        CtModel model = launcher.getModel();

        Map<String, String> chunks = new LinkedHashMap<>();
        for (CtMethod<?> method : model.getElements(new TypeFilter<>(CtMethod.class))) {
            chunks.put(method.getSimpleName(), method.toString());
        }
        return chunks;
    }
}
