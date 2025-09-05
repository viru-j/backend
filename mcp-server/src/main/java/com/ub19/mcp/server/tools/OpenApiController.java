package com.ub19.mcp.server.tools;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ub19.shared.model.dto.GenerateApiRequest;
import com.ub19.shared.model.dto.GenerateApiResponse;
import com.ub19.shared.model.dto.OpenApiSkeletonRequest;
import com.ub19.shared.model.dto.OpenApiSkeletonResponse;

import jakarta.validation.Valid;

/**
 * REST endpoints for OpenAPI-based tooling.
 */
@RestController
@RequestMapping("/tools")
public class OpenApiController {

    private final OpenApiService service;

    public OpenApiController(OpenApiService service) {
        this.service = service;
    }

    @PostMapping("/openapi_to_skeleton")
    public OpenApiSkeletonResponse toSkeleton(@RequestBody @Valid OpenApiSkeletonRequest request) {
        return service.toSkeleton(request.openapiYaml());
    }

    @PostMapping("/generate_api")
    public GenerateApiResponse generate(@RequestBody @Valid GenerateApiRequest request) {
        return service.generate(request.openapiYaml(), request.storyMd(), request.packageBase());
    }
}

