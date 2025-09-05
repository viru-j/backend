package com.ub19.shared.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Represents a single code search hit with file and line information.
 */
public record CodeHit(
        @NotBlank String filePath,
        @Min(1) int lineStart,
        @Min(1) int lineEnd,
        @NotBlank String snippet,
        double score
) {
}

