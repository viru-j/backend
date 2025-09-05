package com.ub19.mcp.server.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import org.springframework.stereotype.Service;

import com.ub19.adapters.neo4j.GraphService;
import com.ub19.mcp.server.git.ChurnService;
import com.ub19.shared.model.dto.DependencyRisk;
import com.ub19.shared.model.dto.ImpactAnalysisResponse;
import com.ub19.shared.model.dto.RiskLevel;

/**
 * Performs impact analysis using graph queries.
 */
@Service
public class ImpactAnalysisService {

    private final GraphService graphService;
    private final ChurnService churnService;

    public ImpactAnalysisService(GraphService graphService, ChurnService churnService) {
        this.graphService = graphService;
        this.churnService = churnService;
    }

    public ImpactAnalysisResponse analyze(String targetFqn, int depth, boolean includeEndpoints) {
        List<String> callers = graphService.findCallers(targetFqn);
        List<String> callees = graphService.findCallees(targetFqn);
        String classFqn = targetFqn.contains(".") ? targetFqn.substring(0, targetFqn.lastIndexOf('.')) : targetFqn;
        List<String> modules = graphService.modulesForClass(classFqn);
        List<String> endpoints = includeEndpoints ? List.of() : List.of();
        int fanOut = callers.size() + callees.size();
        int centrality = fanOut;
        int churn = churnService.churn(targetFqn);
        String risk = String.format("Callers: %d, Callees: %d, Modules: %d, Centrality≈%d, Churn=%d, Tests: none",
                callers.size(), callees.size(), modules.size(), centrality, churn);
        List<DependencyRisk> deps = rankDependencies(callers, callees);
        return new ImpactAnalysisResponse(callers, callees, modules, endpoints, deps, risk);
    }

    private List<DependencyRisk> rankDependencies(List<String> callers, List<String> callees) {
        Set<String> all = new HashSet<>();
        all.addAll(callers);
        all.addAll(callees);
        Map<String, Integer> scores = new HashMap<>();
        for (String dep : all) {
            int degree = graphService.findCallers(dep).size() + graphService.findCallees(dep).size();
            int churn = churnService.churn(dep);
            scores.put(dep, degree + churn);
        }
        List<Map.Entry<String, Integer>> ordered = new ArrayList<>(scores.entrySet());
        ordered.sort(Map.Entry.<String, Integer>comparingByValue().reversed());
        List<DependencyRisk> result = new ArrayList<>();
        for (Map.Entry<String, Integer> e : ordered) {
            result.add(new DependencyRisk(e.getKey(), toLevel(e.getValue())));
        }
        return result;
    }

    private RiskLevel toLevel(int score) {
        if (score >= 10) {
            return RiskLevel.HIGH;
        } else if (score >= 5) {
            return RiskLevel.MEDIUM;
        }
        return RiskLevel.LOW;
    }
}
