package com.ub19.mcp.server.tools;

import static org.mockito.ArgumentMatchers.any;
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

import com.ub19.shared.model.dto.GenerateApiResponse;
import com.ub19.shared.model.dto.OpenApiSkeletonResponse;

@WebMvcTest(OpenApiController.class)
class OpenApiControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    OpenApiService service;

    @Test
    void skeletonEndpointReturnsPackages() throws Exception {
        when(service.toSkeleton(any())).thenReturn(new OpenApiSkeletonResponse(List.of("ok"), Map.of("controller", "p")));
        mvc.perform(post("/tools/openapi_to_skeleton")
                .contentType("application/json")
                .content("{\"openapiYaml\":\"openapi: 3.0.0\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.packages.controller").value("p"));
    }

    @Test
    void generateEndpointReturnsWorkspace() throws Exception {
        when(service.generate(any(), any(), any())).thenReturn(new GenerateApiResponse("/tmp", List.of("pom.xml")));
        mvc.perform(post("/tools/generate_api")
                .contentType("application/json")
                .content("{\"openapiYaml\":\"\",\"packageBase\":\"a\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.workspace").value("/tmp"));
    }
}

