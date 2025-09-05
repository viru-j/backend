package com.ub19.mcp.server.llm;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ub19.shared.model.dto.CodeHit;

/**
 * Deterministic stub LLM client used for tests and local runs.
 */
@Component
public class StubLlmClient implements LlmClient {
    @Override
    public String summarize(String question, List<CodeHit> hits) {
        return "Stub explanation for: " + question;
    }
}
