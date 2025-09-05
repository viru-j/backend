package com.ub19.mcp.server.tools;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.ub19.adapters.neo4j.GraphService;
import com.ub19.shared.model.dto.ImpactAnalysisResponse;

class ImpactAnalysisServiceTest {

    @Test
    void analyzeSummarizesRisk() {
        GraphService graph = mock(GraphService.class);
        when(graph.findCallers("C.m")).thenReturn(List.of("A"));
        when(graph.findCallees("C.m")).thenReturn(List.of("B"));
        when(graph.modulesForClass("C")).thenReturn(List.of("mod"));

        ImpactAnalysisService service = new ImpactAnalysisService(graph);
        ImpactAnalysisResponse resp = service.analyze("C.m", 1, false);

        assertEquals(List.of("A"), resp.callers());
        assertTrue(resp.riskMd().contains("Callers: 1"));
    }
}
