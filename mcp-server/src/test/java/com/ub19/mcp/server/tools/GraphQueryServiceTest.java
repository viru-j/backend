package com.ub19.mcp.server.tools;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.ub19.adapters.neo4j.GraphService;

class GraphQueryServiceTest {

    @Test
    void callersTemplateReturnsRows() {
        GraphService graph = mock(GraphService.class);
        when(graph.findCallers("m")).thenReturn(List.of("a", "b"));

        GraphQueryService service = new GraphQueryService(graph);
        List<Map<String, String>> rows = service.query("callers", Map.of("fqn", "m"));

        assertEquals(2, rows.size());
        assertEquals("a", rows.get(0).get("fqn"));
    }
}
