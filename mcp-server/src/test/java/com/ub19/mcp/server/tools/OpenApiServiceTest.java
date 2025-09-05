package com.ub19.mcp.server.tools;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.List;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.junit.jupiter.api.Test;

import com.ub19.shared.model.dto.GenerateApiResponse;

class OpenApiServiceTest {

    @Test
    void generateProducesCompilableProject() throws Exception {
        OpenApiService service = new OpenApiService();
        String yaml = "openapi: 3.0.0\ninfo:\n  title: Limits API\npaths:\n  /limits:\n    get:\n      operationId: getLimit";
        GenerateApiResponse resp = service.generate(yaml, null, "com.ub19.generated");

        File basedir = new File(resp.workspace());
        assertTrue(resp.files().contains("pom.xml"));

        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(new File(basedir, "pom.xml"));
        request.setGoals(List.of("-q", "package"));
        Invoker invoker = new DefaultInvoker();
        InvocationResult result = invoker.execute(request);
        assertTrue(result.getExitCode() == 0);
    }

    @Test
    void rejectsLargeOpenapi() {
        OpenApiService service = new OpenApiService();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 210000; i++) {
            sb.append('a');
        }
        assertThrows(IllegalArgumentException.class, () -> service.toSkeleton(sb.toString()));
    }
}

