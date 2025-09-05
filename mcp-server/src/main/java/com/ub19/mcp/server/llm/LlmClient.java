package com.ub19.mcp.server.llm;

import java.util.List;

import com.ub19.shared.model.dto.CodeHit;

/**
 * Simple interface for LLM-based summarization.
 */
public interface LlmClient {
    String summarize(String question, List<CodeHit> hits);
}
