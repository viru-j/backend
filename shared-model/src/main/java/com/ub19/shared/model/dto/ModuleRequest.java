package com.ub19.shared.model.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request payload containing a module identifier.
 */
public record ModuleRequest(@NotBlank String module) {
}
