package com.ub19.shared.model.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Aggregated report paths for quality tools.
 */
public record QualityReport(
        @NotBlank String module,
        @NotNull List<ReportFile> surefire,
        @NotNull List<ReportFile> failsafe,
        @NotNull List<ReportFile> pmd,
        @NotNull List<ReportFile> spotbugs,
        @NotNull List<ReportFile> semgrep
) {
}
