package com.ub19.shared.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Request payload for impact analysis operations.
 */
public record ImpactAnalysisRequest(
        @NotBlank String targetFqn,
        @Min(1) @Max(10) int depth,
        boolean includeEndpoints
) {
}
