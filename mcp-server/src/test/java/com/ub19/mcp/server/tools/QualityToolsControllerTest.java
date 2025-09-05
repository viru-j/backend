package com.ub19.mcp.server.tools;

import static org.mockito.ArgumentMatchers.any;
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

import com.ub19.shared.model.dto.QualityReport;
import com.ub19.shared.model.dto.ReportFile;
import com.ub19.shared.model.error.ApiError;
import com.ub19.shared.model.error.ApiException;

@WebMvcTest(QualityToolsController.class)
class QualityToolsControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    QualityToolsService service;

    @Test
    void runTestsReturnsReport() throws Exception {
        QualityReport report = new QualityReport("mod", List.of(new ReportFile("p", false)), List.of(), List.of(), List.of(), List.of());
        when(service.runTests(anyString())).thenReturn(report);
        mvc.perform(post("/tools/run_tests").contentType(MediaType.APPLICATION_JSON).content("{\"module\":\"mod\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.surefire[0].path").value("p"));
    }

    @Test
    void qualityReportErrorMapsStatus() throws Exception {
        when(service.qualityReport(anyString(), any())).thenThrow(new ApiException(org.springframework.http.HttpStatus.BAD_REQUEST, new ApiError("e", "m")));
        mvc.perform(post("/tools/quality_report").contentType(MediaType.APPLICATION_JSON).content("{\"module\":\"m\"}"))
                .andExpect(status().isBadRequest());
    }
}
