package com.ub19.orchestrator.client;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.ub19.orchestrator.config.McpClientProperties;
import com.ub19.shared.model.dto.ExplainRequest;
import com.ub19.shared.model.dto.ExplainResponse;
import com.ub19.shared.model.dto.GenerateApiRequest;
import com.ub19.shared.model.dto.GenerateApiResponse;
import com.ub19.shared.model.dto.GraphQueryRequest;
import com.ub19.shared.model.dto.GraphQueryResponse;
import com.ub19.shared.model.dto.ImpactAnalysisRequest;
import com.ub19.shared.model.dto.ImpactAnalysisResponse;
import com.ub19.shared.model.dto.OpenApiSkeletonRequest;
import com.ub19.shared.model.dto.OpenApiSkeletonResponse;
import com.ub19.shared.model.dto.SearchRequest;
import com.ub19.shared.model.dto.SearchResponse;

/**
 * Thin HTTP client for MCP server tools.
 */
@Component
public class ToolClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public ToolClient(RestTemplate restTemplate, McpClientProperties properties) {
        this.restTemplate = restTemplate;
        this.baseUrl = properties.getBaseUrl();
    }

    public SearchResponse searchCode(String query, int topK) {
        SearchRequest req = new SearchRequest(query, topK);
        return restTemplate.postForObject(baseUrl + "/tools/search_code", req, SearchResponse.class);
    }

    public ExplainResponse explainCode(String question, int topK) {
        ExplainRequest req = new ExplainRequest(question, topK);
        return restTemplate.postForObject(baseUrl + "/tools/explain_code", req, ExplainResponse.class);
    }

    public OpenApiSkeletonResponse openapiToSkeleton(String yaml) {
        OpenApiSkeletonRequest req = new OpenApiSkeletonRequest(yaml);
        return restTemplate.postForObject(baseUrl + "/tools/openapi_to_skeleton", req, OpenApiSkeletonResponse.class);
    }

    public GenerateApiResponse generateApi(String openapiYaml, String storyMd, String packageBase) {
        GenerateApiRequest req = new GenerateApiRequest(openapiYaml, storyMd, packageBase);
        return restTemplate.postForObject(baseUrl + "/tools/generate_api", req, GenerateApiResponse.class);
    }

    public ImpactAnalysisResponse impactAnalysis(String targetFqn, int depth, boolean includeEndpoints) {
        ImpactAnalysisRequest req = new ImpactAnalysisRequest(targetFqn, depth, includeEndpoints);
        return restTemplate.postForObject(baseUrl + "/tools/impact_analysis", req, ImpactAnalysisResponse.class);
    }

    public GraphQueryResponse graphQuery(String templateKey, Map<String, String> params) {
        GraphQueryRequest req = new GraphQueryRequest(templateKey, params);
        return restTemplate.postForObject(baseUrl + "/tools/graph_query", req, GraphQueryResponse.class);
    }
}
