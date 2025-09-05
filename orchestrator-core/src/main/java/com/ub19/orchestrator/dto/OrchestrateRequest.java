package com.ub19.orchestrator.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request payload to orchestrate a use case.
 */
public record OrchestrateRequest(
        @NotBlank String uc,
        @NotBlank String input
) {}
