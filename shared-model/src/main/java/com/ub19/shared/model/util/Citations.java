package com.ub19.shared.model.util;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility for canonicalizing file path citations.
 */
public final class Citations {

    private static final Pattern RAW = Pattern.compile("^(?<path>.+?):L?(?<start>\\d+)(?:-L?(?<end>\\d+))?$");

    private Citations() {
    }

    /**
     * Formats a citation using a file path and line numbers.
     * Paths are normalized and backslashes converted to forward slashes.
     *
     * @param filePath the file path
     * @param lineStart starting line number (1-indexed)
     * @param lineEnd ending line number (1-indexed)
     * @return canonical citation string
     */
    public static String canonicalize(String filePath, int lineStart, int lineEnd) {
        String normalized = normalizePath(filePath);
        if (lineStart <= 0 || lineEnd <= 0) {
            throw new IllegalArgumentException("Line numbers must be positive");
        }
        if (lineEnd < lineStart) {
            int tmp = lineStart;
            lineStart = lineEnd;
            lineEnd = tmp;
        }
        if (lineStart == lineEnd) {
            return normalized + ":L" + lineStart;
        }
        return normalized + ":L" + lineStart + "-L" + lineEnd;
    }

    /**
     * Canonicalizes an existing citation string. Accepts paths using either
     * forward or backward slashes and line numbers with or without the
     * leading 'L'.
     *
     * @param citation raw citation string
     * @return canonical citation string
     */
    public static String canonicalize(String citation) {
        Matcher m = RAW.matcher(citation);
        if (!m.matches()) {
            return canonicalize(citation, 1, 1); // treat whole citation as path
        }
        String path = m.group("path");
        int start = Integer.parseInt(m.group("start"));
        String endGroup = m.group("end");
        int end = endGroup != null ? Integer.parseInt(endGroup) : start;
        return canonicalize(path, start, end);
    }

    private static String normalizePath(String path) {
        String replaced = path.replace('\\', '/');
        Path p = Paths.get(replaced).normalize();
        return p.toString().replace('\\', '/');
    }
}

