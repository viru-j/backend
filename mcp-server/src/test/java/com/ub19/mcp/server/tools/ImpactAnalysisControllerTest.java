package com.ub19.mcp.server.tools;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.ub19.shared.model.dto.ImpactAnalysisResponse;

@WebMvcTest(ImpactAnalysisController.class)
class ImpactAnalysisControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    ImpactAnalysisService service;

    @Test
    void postReturnsAnalysis() throws Exception {
        ImpactAnalysisResponse resp = new ImpactAnalysisResponse(List.of(), List.of(), List.of(), List.of(), List.of(), "r");
        when(service.analyze(anyString(), anyInt(), anyBoolean())).thenReturn(resp);
        mvc.perform(post("/tools/impact_analysis")
                .contentType("application/json")
                .content("{\"targetFqn\":\"C.m\",\"depth\":1,\"includeEndpoints\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.riskMd").value("r"));
    }
}
