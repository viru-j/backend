package com.ub19.mcp.server.tools;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ub19.shared.model.dto.ExplainRequest;
import com.ub19.shared.model.dto.ExplainResponse;

import jakarta.validation.Valid;

/**
 * REST endpoint exposing the explain_code tool.
 */
@RestController
@RequestMapping("/tools")
public class ExplainCodeController {

    private final ExplainCodeService service;

    public ExplainCodeController(ExplainCodeService service) {
        this.service = service;
    }

    @PostMapping("/explain_code")
    public ExplainResponse explain(@RequestBody @Valid ExplainRequest request) {
        return service.explain(request.question(), request.topK());
    }
}
