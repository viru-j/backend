package com.ub19.quality;

import java.util.List;
import java.util.stream.Stream;

/**
 * Aggregated report paths for quality tools.
 */
public record QualityReport(
        String module,
        List<ReportFile> surefire,
        List<ReportFile> failsafe,
        List<ReportFile> pmd,
        List<ReportFile> spotbugs,
        List<ReportFile> semgrep) {

    public boolean hasBlockers() {
        return Stream.of(surefire, failsafe, pmd, spotbugs, semgrep)
                .flatMap(List::stream)
                .anyMatch(ReportFile::blocker);
    }
}
