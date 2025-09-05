package com.ub19.orchestrator;

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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.ub19.orchestrator.controller.OrchestrateController;
import com.ub19.orchestrator.dto.OrchestrateResponse;
import com.ub19.orchestrator.service.OrchestratorService;

@WebMvcTest(OrchestrateController.class)
class OrchestrateControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    OrchestratorService service;

    @Test
    void postReturnsResponse() throws Exception {
        when(service.orchestrate(anyString(), anyString())).thenReturn(new OrchestrateResponse("ans", List.of("c")));
        mvc.perform(post("/orchestrate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"uc\":\"UC1\",\"input\":\"q\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answerMd").value("ans"));
    }
}
