package com.ub19.shared.model.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request payload for generating a quality report.
 */
public record QualityReportRequest(
        @NotBlank String module,
        String branch
) {
}
