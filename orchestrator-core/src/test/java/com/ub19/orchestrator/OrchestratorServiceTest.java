package com.ub19.orchestrator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ub19.orchestrator.audit.AuditService;
import com.ub19.orchestrator.client.ToolClient;
import com.ub19.orchestrator.config.McpClientProperties;
import com.ub19.orchestrator.service.OrchestratorService;
import com.ub19.orchestrator.dto.OrchestrateResponse;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import org.springframework.http.HttpMethod;

class OrchestratorServiceTest {

    @Test
    void uc1FlowInvokesTools() {
        RestTemplate rest = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.createServer(rest);
        server.expect(requestTo("http://mcp/tools/search_code"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{\"hits\":[]}", MediaType.APPLICATION_JSON));
        server.expect(requestTo("http://mcp/tools/explain_code"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{\"explanationMd\":\"e\",\"citations\":[\"src\\\\F.java:1-2\"]}", MediaType.APPLICATION_JSON));

        McpClientProperties props = new McpClientProperties();
        props.setBaseUrl("http://mcp");
        ToolClient client = new ToolClient(rest, props);
        AuditService audit = spy(new AuditService(new ObjectMapper()));
        OrchestratorService service = new OrchestratorService(client, audit, new com.ub19.orchestrator.service.PromptTemplates());

        OrchestrateResponse resp = service.orchestrate("UC1", "q");

        assertEquals("e", resp.answerMd());
        assertEquals("src/F.java:L1-L2", resp.citations().get(0));
        verify(audit, org.mockito.Mockito.times(2)).record(any(), any(), any());
    }
}
