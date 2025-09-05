package com.ub19.mcp.server.tools;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ub19.mcp.server.llm.LlmClient;
import com.ub19.shared.model.dto.CodeHit;
import com.ub19.shared.model.dto.ExplainResponse;
import com.ub19.shared.model.util.Citations;

/**
 * Orchestrates code search and summarization for explanations.
 */
@Service
public class ExplainCodeService {

    private final SearchCodeService searchService;
    private final LlmClient llmClient;

    public ExplainCodeService(SearchCodeService searchService, LlmClient llmClient) {
        this.searchService = searchService;
        this.llmClient = llmClient;
    }

    public ExplainResponse explain(String question, int topK) {
        List<CodeHit> hits = searchService.search(question, topK);
        String explanation = llmClient.summarize(question, hits);
        List<String> citations = hits.stream()
                .map(hit -> Citations.canonicalize(hit.filePath(), hit.lineStart(), hit.lineEnd()))
                .collect(Collectors.toList());
        return new ExplainResponse(explanation, citations);
    }
}
