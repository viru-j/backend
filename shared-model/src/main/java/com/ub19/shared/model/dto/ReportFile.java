package com.ub19.shared.model.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Descriptor of a report file and whether it contains blocker findings.
 */
public record ReportFile(
        @NotBlank String path,
        boolean blocker
) {
}
