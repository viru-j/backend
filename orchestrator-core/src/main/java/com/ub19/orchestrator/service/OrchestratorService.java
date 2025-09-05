package com.ub19.orchestrator.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ub19.orchestrator.audit.AuditService;
import com.ub19.orchestrator.client.ToolClient;
import com.ub19.orchestrator.dto.OrchestrateResponse;
import com.ub19.shared.model.dto.ExplainResponse;
import com.ub19.shared.model.dto.GenerateApiResponse;
import com.ub19.shared.model.dto.GraphQueryResponse;
import com.ub19.shared.model.dto.ImpactAnalysisResponse;
import com.ub19.shared.model.dto.OpenApiSkeletonResponse;
import com.ub19.shared.model.dto.SearchResponse;

/**
 * High level orchestration with a simple step budget and policy per use case.
 */
@Service
public class OrchestratorService {

    private final ToolClient tools;
    private final AuditService audit;
    private final PromptTemplates templates;
    private final int stepBudget = 5;

    public OrchestratorService(ToolClient tools, AuditService audit, PromptTemplates templates) {
        this.tools = tools;
        this.audit = audit;
        this.templates = templates;
    }

    public OrchestrateResponse orchestrate(String uc, String input) {
        switch (uc) {
        case "UC1":
            return handleUc1(input);
        case "UC2":
            return handleUc2(input);
        case "UC3":
            return handleUc3(input);
        case "UC4":
            return handleUc4(input);
        default:
            return new OrchestrateResponse("Unknown use case: " + uc, List.of());
        }
    }

    private OrchestrateResponse handleUc1(String question) {
        StepCounter counter = new StepCounter();
        String prompt = templates.render("UC1", question);
        SearchResponse sr = counter.call(() -> tools.searchCode(prompt, 5));
        ExplainResponse er = counter.call(() -> tools.explainCode(prompt, 5));
        audit.record("search_code", question, sr);
        audit.record("explain_code", question, er);
        return new OrchestrateResponse(er.explanationMd(), er.citations());
    }

    private OrchestrateResponse handleUc2(String openapiYaml) {
        StepCounter counter = new StepCounter();
        String prompt = templates.render("UC2", openapiYaml);
        OpenApiSkeletonResponse sk = counter.call(() -> tools.openapiToSkeleton(prompt));
        GenerateApiResponse ga = counter.call(() -> tools.generateApi(prompt, null, sk.packages().get("controller")));
        audit.record("openapi_to_skeleton", openapiYaml, sk);
        audit.record("generate_api", openapiYaml, ga);
        String md = "Generated workspace: " + ga.workspace();
        return new OrchestrateResponse(md, List.of());
    }

    private OrchestrateResponse handleUc3(String targetFqn) {
        StepCounter counter = new StepCounter();
        String prompt = templates.render("UC3", targetFqn);
        ImpactAnalysisResponse ia = counter.call(() -> tools.impactAnalysis(prompt, 1, false));
        audit.record("impact_analysis", targetFqn, ia);
        return new OrchestrateResponse(ia.riskMd(), ia.endpoints());
    }

    private OrchestrateResponse handleUc4(String fqn) {
        StepCounter counter = new StepCounter();
        String prompt = templates.render("UC4", fqn);
        GraphQueryResponse gr = counter.call(() -> tools.graphQuery("callers", Map.of("fqn", prompt)));
        audit.record("graph_query", fqn, gr);
        String md = gr.rows().isEmpty() ? "No callers" : "Found callers";
        return new OrchestrateResponse(md, List.of());
    }

    private class StepCounter {
        private int steps = 0;
        <T> T call(SupplierWithException<T> supplier) {
            if (++steps > stepBudget) {
                throw new IllegalStateException("Step budget exceeded");
            }
            return supplier.get();
        }
    }

    @FunctionalInterface
    private interface SupplierWithException<T> {
        T get();
    }
}
