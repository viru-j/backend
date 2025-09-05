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
import com.ub19.shared.model.util.Citations;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

/**
 * High level orchestration with a simple step budget and policy per use case.
 */
@Service
public class OrchestratorService {

    private final ToolClient tools;
    private final AuditService audit;
    private final PromptTemplates templates;
    private final MeterRegistry registry;
    private final int stepBudget = 5;

    public OrchestratorService(ToolClient tools, AuditService audit, PromptTemplates templates, MeterRegistry registry) {
        this.tools = tools;
        this.audit = audit;
        this.templates = templates;
        this.registry = registry;
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
        StepCounter counter = new StepCounter("UC1");
        String prompt = templates.render("UC1", question);
        SearchResponse sr = counter.call("search_code", () -> tools.searchCode(prompt, 5));
        ExplainResponse er = counter.call("explain_code", () -> tools.explainCode(prompt, 5));
        List<String> canonical = er.citations().stream()
                .map(Citations::canonicalize)
                .toList();
        return new OrchestrateResponse(er.explanationMd(), canonical);
    }

    private OrchestrateResponse handleUc2(String openapiYaml) {
        StepCounter counter = new StepCounter("UC2");
        String prompt = templates.render("UC2", openapiYaml);
        OpenApiSkeletonResponse sk = counter.call("openapi_to_skeleton", () -> tools.openapiToSkeleton(prompt));
        GenerateApiResponse ga = counter.call("generate_api", () -> tools.generateApi(prompt, null, sk.packages().get("controller")));
        String md = "Generated workspace: " + ga.workspace();
        return new OrchestrateResponse(md, List.of());
    }

    private OrchestrateResponse handleUc3(String targetFqn) {
        StepCounter counter = new StepCounter("UC3");
        String prompt = templates.render("UC3", targetFqn);
        ImpactAnalysisResponse ia = counter.call("impact_analysis", () -> tools.impactAnalysis(prompt, 1, false));
        return new OrchestrateResponse(ia.riskMd(), ia.endpoints());
    }

    private OrchestrateResponse handleUc4(String fqn) {
        StepCounter counter = new StepCounter("UC4");
        String prompt = templates.render("UC4", fqn);
        GraphQueryResponse gr = counter.call("graph_query", () -> tools.graphQuery("callers", Map.of("fqn", prompt)));
        String md = gr.rows().isEmpty() ? "No callers" : "Found callers";
        return new OrchestrateResponse(md, List.of());
    }

    private class StepCounter {
        private final String uc;
        private int steps = 0;

        StepCounter(String uc) {
            this.uc = uc;
        }

        <T> T call(String tool, SupplierWithException<T> supplier) {
            if (++steps > stepBudget) {
                throw new IllegalStateException("Step budget exceeded");
            }
            Timer.Sample sample = Timer.start(registry);
            T resp = supplier.get();
            long duration = sample.stop(registry.timer("tool.calls", "tool", tool));
            audit.record(uc, tool, duration, countHits(resp));
            return resp;
        }
    }

    private int countHits(Object resp) {
        if (resp instanceof SearchResponse sr) {
            return sr.hits().size();
        }
        if (resp instanceof ExplainResponse er) {
            return er.citations().size();
        }
        if (resp instanceof GraphQueryResponse gr) {
            return gr.rows().size();
        }
        if (resp instanceof ImpactAnalysisResponse ia) {
            return ia.callers().size() + ia.callees().size();
        }
        if (resp instanceof OpenApiSkeletonResponse sk) {
            return sk.checklist().size();
        }
        if (resp instanceof GenerateApiResponse ga) {
            return ga.files().size();
        }
        return 0;
    }

    @FunctionalInterface
    private interface SupplierWithException<T> {
        T get();
    }
}
