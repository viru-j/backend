package com.ub19.shared.model.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * Response containing validation checklist and suggested package names.
 */
public record OpenApiSkeletonResponse(
        @NotEmpty List<String> checklist,
        @NotNull Map<String, String> packages
) {
}

