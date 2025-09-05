package com.ub19.orchestrator;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.ub19.orchestrator.client.ToolClient;
import com.ub19.orchestrator.service.OrchestratorService;
import com.ub19.shared.model.dto.CodeHit;
import com.ub19.shared.model.dto.ExplainResponse;
import com.ub19.shared.model.dto.SearchResponse;

import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

/**
 * Verifies JSON logging and metric registration.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith({OutputCaptureExtension.class, SpringExtension.class})
class AuditLoggingMetricsTest {

    @Autowired
    OrchestratorService service;

    @Autowired
    MockMvc mvc;

    @MockBean
    ToolClient tools;

    @Test
    void logsJsonAndMetrics(CapturedOutput output) throws Exception {
        when(tools.searchCode(anyString(), anyInt())).thenReturn(new SearchResponse(List.of(new CodeHit("F.java", 1, 2, "s", 1f))));
        when(tools.explainCode(anyString(), anyInt())).thenReturn(new ExplainResponse("e", List.of("F.java:L1-L2")));

        service.orchestrate("UC1", "q");

        String logs = output.toString();
        assertTrue(logs.contains("\"actor\":\"system\""));
        assertTrue(logs.contains("\"tool\":\"search_code\""));

        mvc.perform(get("/actuator/metrics/tool.calls"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("tool.calls"));
    }
}

