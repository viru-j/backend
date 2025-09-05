package com.ub19.mcp.server.tools;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.ub19.adapters.neo4j.GraphService;
import com.ub19.mcp.server.git.ChurnService;
import com.ub19.shared.model.dto.DependencyRisk;
import com.ub19.shared.model.dto.RiskLevel;

class ImpactAnalysisServiceTest {

    @Test
    void ranksDependenciesAndLabelsRisk() {
        GraphService graph = mock(GraphService.class);
        ChurnService churn = mock(ChurnService.class);

        when(graph.findCallers("C.m")).thenReturn(List.of("A.m1", "B.m2"));
        when(graph.findCallees("C.m")).thenReturn(List.of("D.m3"));
        when(graph.modulesForClass("C")).thenReturn(List.of("mod"));

        when(graph.findCallers("A.m1")).thenReturn(List.of("X"));
        when(graph.findCallees("A.m1")).thenReturn(List.of());
        when(graph.findCallers("B.m2")).thenReturn(List.of());
        when(graph.findCallees("B.m2")).thenReturn(List.of("Y", "Z"));
        when(graph.findCallers("D.m3")).thenReturn(List.of("Q", "R", "S"));
        when(graph.findCallees("D.m3")).thenReturn(List.of());

        when(churn.churn("C.m")).thenReturn(4);
        when(churn.churn("A.m1")).thenReturn(6);
        when(churn.churn("B.m2")).thenReturn(0);
        when(churn.churn("D.m3")).thenReturn(9);

        ImpactAnalysisService service = new ImpactAnalysisService(graph, churn);
        var resp = service.analyze("C.m", 1, false);

        assertTrue(resp.riskMd().contains("Churn=4"));
        assertEquals(3, resp.dependencies().size());
        DependencyRisk first = resp.dependencies().get(0);
        assertEquals("D.m3", first.fqn());
        assertEquals(RiskLevel.HIGH, first.risk());
        DependencyRisk second = resp.dependencies().get(1);
        assertEquals("A.m1", second.fqn());
        assertEquals(RiskLevel.MEDIUM, second.risk());
        DependencyRisk third = resp.dependencies().get(2);
        assertEquals(RiskLevel.LOW, third.risk());
    }
}
