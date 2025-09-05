package com.ub19.shared.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * Response describing generated API skeleton files and workspace location.
 */
public record GenerateApiResponse(
        @NotBlank String workspace,
        @NotEmpty List<String> files
) {
}

