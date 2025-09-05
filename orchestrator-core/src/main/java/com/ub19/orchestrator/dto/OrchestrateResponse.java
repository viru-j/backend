package com.ub19.orchestrator.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Response payload produced by the orchestrator.
 */
public record OrchestrateResponse(
        @NotBlank String answerMd,
        @NotNull List<String> citations
) {}
