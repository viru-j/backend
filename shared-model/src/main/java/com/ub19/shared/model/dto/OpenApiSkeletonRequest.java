package com.ub19.shared.model.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request payload for converting OpenAPI definitions into project skeleton hints.
 */
public record OpenApiSkeletonRequest(@NotBlank String openapiYaml) {
}

