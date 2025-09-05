package com.ub19.shared.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * Response describing impact analysis results.
 */
public record ImpactAnalysisResponse(
        @NotNull List<String> callers,
        @NotNull List<String> callees,
        @NotNull List<String> modules,
        @NotNull List<String> endpoints,
        @NotBlank String riskMd
) {
}

