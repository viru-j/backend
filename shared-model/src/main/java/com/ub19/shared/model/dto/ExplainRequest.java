package com.ub19.shared.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for explain code operations.
 */
public record ExplainRequest(
        @NotBlank @Size(max = 1000) String question,
        @Min(1) @Max(100) int topK
) {
}
