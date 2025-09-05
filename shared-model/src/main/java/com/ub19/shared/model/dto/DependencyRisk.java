package com.ub19.shared.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Dependency annotated with a risk level.
 */
public record DependencyRisk(
        @NotBlank String fqn,
        @NotNull RiskLevel risk
) {
}
