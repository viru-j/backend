package com.ub19.mcp.server.tools;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ub19.adapters.neo4j.GraphService;
import com.ub19.shared.model.dto.ImpactAnalysisResponse;

/**
 * Performs impact analysis using graph queries.
 */
@Service
public class ImpactAnalysisService {

    private final GraphService graphService;

    public ImpactAnalysisService(GraphService graphService) {
        this.graphService = graphService;
    }

    public ImpactAnalysisResponse analyze(String targetFqn, int depth, boolean includeEndpoints) {
        List<String> callers = graphService.findCallers(targetFqn);
        List<String> callees = graphService.findCallees(targetFqn);
        String classFqn = targetFqn.contains(".") ? targetFqn.substring(0, targetFqn.lastIndexOf('.')) : targetFqn;
        List<String> modules = graphService.modulesForClass(classFqn);
        List<String> endpoints = includeEndpoints ? List.of() : List.of();
        int fanOut = callers.size() + callees.size();
        int centrality = fanOut;
        String risk = String.format("Callers: %d, Callees: %d, Modules: %d, Centrality≈%d, Tests: none",
                callers.size(), callees.size(), modules.size(), centrality);
        return new ImpactAnalysisResponse(callers, callees, modules, endpoints, risk);
    }
}
