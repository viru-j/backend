package com.ub19.quality;

/**
 * Descriptor of a report file and whether it contains blocker findings.
 */
public record ReportFile(String path, boolean blocker) {
}
