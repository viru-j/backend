package com.ub19.shared.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for converting OpenAPI definitions into project skeleton hints.
 */
public record OpenApiSkeletonRequest(@NotBlank @Size(max = 200000) String openapiYaml) {
}

