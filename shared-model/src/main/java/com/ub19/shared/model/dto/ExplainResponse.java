package com.ub19.shared.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * DTO returned by explain code operations.
 */
public record ExplainResponse(
        @NotBlank String explanation,
        @NotNull List<@Valid CodeHit> citations
) {
}

