package com.ub19.orchestrator.service;

import org.springframework.stereotype.Component;

/**
 * Simple prompt templates per use case.
 */
@Component
public class PromptTemplates {
    public String render(String uc, String input) {
        return switch (uc) {
        case "UC1" -> "Explain: " + input;
        case "UC2" -> input;
        case "UC3" -> input;
        case "UC4" -> input;
        default -> input;
        };
    }
}
