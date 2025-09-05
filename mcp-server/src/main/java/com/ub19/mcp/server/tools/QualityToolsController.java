package com.ub19.mcp.server.tools;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ub19.shared.model.dto.ModuleRequest;
import com.ub19.shared.model.dto.QualityReport;
import com.ub19.shared.model.dto.QualityReportRequest;

import jakarta.validation.Valid;

/**
 * REST endpoints for quality related tools.
 */
@RestController
@RequestMapping("/tools")
public class QualityToolsController {

    private final QualityToolsService service;

    public QualityToolsController(QualityToolsService service) {
        this.service = service;
    }

    @PostMapping("/run_tests")
    public QualityReport runTests(@RequestBody @Valid ModuleRequest request) {
        return service.runTests(request.module());
    }

    @PostMapping("/static_scan")
    public QualityReport staticScan(@RequestBody @Valid ModuleRequest request) {
        return service.staticScan(request.module());
    }

    @PostMapping("/quality_report")
    public QualityReport qualityReport(@RequestBody @Valid QualityReportRequest request) {
        return service.qualityReport(request.module(), request.branch());
    }
}
