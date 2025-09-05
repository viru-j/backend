package com.ub19.shared.model.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request payload for API skeleton generation.
 */
public record GenerateApiRequest(
        String openapiYaml,
        String storyMd,
        @NotBlank String packageBase
) {
}

