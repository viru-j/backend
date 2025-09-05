package com.ub19.mcp.server.tools;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.ub19.mcp.server.llm.LlmClient;
import com.ub19.shared.model.dto.CodeHit;
import com.ub19.shared.model.dto.ExplainResponse;

class ExplainCodeServiceTest {

    @Test
    void explainFormatsCitations() {
        SearchCodeService search = mock(SearchCodeService.class);
        LlmClient llm = mock(LlmClient.class);
        List<CodeHit> hits = List.of(new CodeHit("F.java", 1, 2, "snip", 1f));
        when(search.search(eq("q"), eq(2))).thenReturn(hits);
        when(llm.summarize("q", hits)).thenReturn("exp");

        ExplainCodeService service = new ExplainCodeService(search, llm);
        ExplainResponse resp = service.explain("q", 2);

        assertEquals("exp", resp.explanationMd());
        assertEquals(List.of("F.java:1-2"), resp.citations());
    }
}
