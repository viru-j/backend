package com.ub19.mcp.server.tools;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(GraphQueryController.class)
class GraphQueryControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    GraphQueryService service;

    @Test
    void postReturnsRows() throws Exception {
        when(service.query(eq("callers"), anyMap())).thenReturn(List.of(Map.of("fqn", "x")));
        mvc.perform(post("/tools/graph_query")
                .contentType("application/json")
                .content("{\"templateKey\":\"callers\",\"params\":{\"fqn\":\"m\"}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows[0].fqn").value("x"));
    }
}
