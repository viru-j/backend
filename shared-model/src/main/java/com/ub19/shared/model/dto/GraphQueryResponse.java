package com.ub19.shared.model.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * Response payload for graph queries.
 */
public record GraphQueryResponse(
        @NotNull List<Map<String, String>> rows
) {
}
