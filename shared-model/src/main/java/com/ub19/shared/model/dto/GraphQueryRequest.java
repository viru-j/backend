package com.ub19.shared.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

/**
 * Request payload for graph queries based on allow-listed templates.
 */
public record GraphQueryRequest(
        @NotBlank String templateKey,
        @NotNull Map<String, String> params
) {
}
