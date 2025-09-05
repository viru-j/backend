package com.ub19.mcp.server.tools;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ub19.shared.model.dto.GraphQueryRequest;
import com.ub19.shared.model.dto.GraphQueryResponse;

import jakarta.validation.Valid;

/**
 * REST endpoint exposing the graph_query tool.
 */
@RestController
@RequestMapping("/tools")
public class GraphQueryController {

    private final GraphQueryService service;

    public GraphQueryController(GraphQueryService service) {
        this.service = service;
    }

    @PostMapping("/graph_query")
    public GraphQueryResponse query(@RequestBody @Valid GraphQueryRequest request) {
        List<Map<String, String>> rows = service.query(request.templateKey(), request.params());
        return new GraphQueryResponse(rows);
    }
}
