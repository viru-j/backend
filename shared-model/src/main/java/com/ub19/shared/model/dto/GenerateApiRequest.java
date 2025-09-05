package com.ub19.shared.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for API skeleton generation.
 */
public record GenerateApiRequest(
        @Size(max = 200000) String openapiYaml,
        @Size(max = 200000) String storyMd,
        @NotBlank String packageBase
) {
}

