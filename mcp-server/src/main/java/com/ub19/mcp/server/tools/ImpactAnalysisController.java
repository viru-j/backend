package com.ub19.mcp.server.tools;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ub19.shared.model.dto.ImpactAnalysisRequest;
import com.ub19.shared.model.dto.ImpactAnalysisResponse;

import jakarta.validation.Valid;

/**
 * REST endpoint exposing the impact_analysis tool.
 */
@RestController
@RequestMapping("/tools")
public class ImpactAnalysisController {

    private final ImpactAnalysisService service;

    public ImpactAnalysisController(ImpactAnalysisService service) {
        this.service = service;
    }

    @PostMapping("/impact_analysis")
    public ImpactAnalysisResponse analyze(@RequestBody @Valid ImpactAnalysisRequest request) {
        return service.analyze(request.targetFqn(), request.depth(), request.includeEndpoints());
    }
}
