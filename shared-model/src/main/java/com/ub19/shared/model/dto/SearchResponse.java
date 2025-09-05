package com.ub19.shared.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * Response wrapper for code search results.
 */
public record SearchResponse(
        @NotEmpty List<@Valid CodeHit> hits
) {
}

