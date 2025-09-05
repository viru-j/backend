package com.ub19.shared.model.error;

import jakarta.validation.constraints.NotBlank;

/**
 * Common error payload used across services.
 */
public record ApiError(
        @NotBlank String code,
        @NotBlank String message
) {
}

