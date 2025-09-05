package com.ub19.mcp.server.tools;

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

import com.ub19.shared.model.dto.ExplainResponse;

@WebMvcTest(ExplainCodeController.class)
class ExplainCodeControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    ExplainCodeService service;

    @Test
    void postReturnsExplanation() throws Exception {
        when(service.explain(anyString(), anyInt())).thenReturn(new ExplainResponse("exp", List.of("F.java:L1-L2")));
        mvc.perform(post("/tools/explain_code")
                .contentType("application/json")
                .content("{\"question\":\"q\",\"topK\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.explanationMd").value("exp"))
                .andExpect(jsonPath("$.citations[0]").value("F.java:L1-L2"));
    }
}
