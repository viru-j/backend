package com.ub19.orchestrator.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ub19.orchestrator.dto.OrchestrateRequest;
import com.ub19.orchestrator.dto.OrchestrateResponse;
import com.ub19.orchestrator.service.OrchestratorService;

import jakarta.validation.Valid;

/**
 * REST endpoint for orchestrating multi-step use cases.
 */
@RestController
@RequestMapping("/orchestrate")
public class OrchestrateController {

    private final OrchestratorService service;

    public OrchestrateController(OrchestratorService service) {
        this.service = service;
    }

    @PostMapping
    public OrchestrateResponse orchestrate(@RequestBody @Valid OrchestrateRequest request) {
        return service.orchestrate(request.uc(), request.input());
    }
}
