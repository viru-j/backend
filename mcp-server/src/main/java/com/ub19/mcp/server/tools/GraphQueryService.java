package com.ub19.mcp.server.tools;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ub19.adapters.neo4j.GraphService;
import com.ub19.shared.model.error.ApiError;
import com.ub19.shared.model.error.ApiException;

/**
 * Executes allow-listed graph queries against Neo4j.
 */
@Service
public class GraphQueryService {

    private final Map<String, Function<Map<String, String>, List<Map<String, String>>>> templates;

    public GraphQueryService(GraphService graphService) {
        this.templates = Map.of(
                "callers", params -> graphService.findCallers(params.get("fqn")).stream()
                        .map(f -> Map.of("fqn", f))
                        .toList(),
                "callees", params -> graphService.findCallees(params.get("fqn")).stream()
                        .map(f -> Map.of("fqn", f))
                        .toList(),
                "endpointsForRule", params -> graphService.endpointsForRule(params.get("ruleKey")).stream()
                        .map(e -> Map.of("path", e.path(), "http", e.http(), "methodFqn", e.methodFqn()))
                        .toList(),
                "modulesForClass", params -> graphService.modulesForClass(params.get("fqn")).stream()
                        .map(m -> Map.of("module", m))
                        .toList());
    }

    public List<Map<String, String>> query(String templateKey, Map<String, String> params) {
        Function<Map<String, String>, List<Map<String, String>>> exec = templates.get(templateKey);
        if (exec == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    new ApiError("UNKNOWN_TEMPLATE", "Unknown template key: " + templateKey));
        }
        return exec.apply(params);
    }
}
