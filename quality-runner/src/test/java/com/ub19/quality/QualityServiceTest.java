package com.ub19.quality;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

import org.apache.maven.shared.invoker.InvocationOutputHandler;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.InvokerLogger;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

class QualityServiceTest {

    @Test
    void aggregatesReportsAndReturnsZeroWhenNoBlockers() throws Exception {
        Path module = setupModule("{}\n");
        QualityService service = new QualityService(new StubInvoker(0), new ObjectMapper());
        int code = service.run(module);
        assertEquals(0, code);
        assertTrue(Files.exists(module.resolve("quality-report.json")));
    }

    @Test
    void returnsNonZeroWhenBlockerFound() throws Exception {
        Path module = setupModule("{\"level\":\"error\"}\n");
        QualityService service = new QualityService(new StubInvoker(0), new ObjectMapper());
        int code = service.run(module);
        assertEquals(1, code);
    }

    private Path setupModule(String spotbugsContent) throws Exception {
        Path module = Files.createTempDirectory("module");
        Files.writeString(module.resolve("pom.xml"), "<project></project>");
        Files.createDirectories(module.resolve("target/surefire-reports"));
        Files.writeString(module.resolve("target/surefire-reports/TEST-a.xml"), "<testsuite></testsuite>");
        Files.createDirectories(module.resolve("target/sarif"));
        Files.writeString(module.resolve("target/sarif/spotbugs.sarif"), spotbugsContent);
        return module;
    }

    static class StubInvoker implements Invoker {
        private final int code;
        StubInvoker(int code) { this.code = code; }
        @Override
        public InvocationResult execute(InvocationRequest request) { return new InvocationResult() {
            @Override public int getExitCode() { return code; }
            @Override public Throwable getExecutionException() { return null; }
        }; }
        @Override public Invoker setWorkingDirectory(File workingDirectory) { return this; }
        @Override public File getWorkingDirectory() { return null; }
        @Override public Invoker setMavenHome(File mavenHome) { return this; }
        @Override public File getMavenHome() { return null; }
        @Override public Invoker setLocalRepositoryDirectory(File localRepositoryDirectory) { return this; }
        @Override public File getLocalRepositoryDirectory() { return null; }
        @Override public Invoker setLogger(InvokerLogger logger) { return this; }
        @Override public InvokerLogger getLogger() { return null; }
        @Override public Invoker setOutputHandler(InvocationOutputHandler handler) { return this; }
        @Override public Invoker setErrorHandler(InvocationOutputHandler handler) { return this; }
        @Override public InvocationOutputHandler getOutputHandler() { return null; }
        @Override public InvocationOutputHandler getErrorHandler() { return null; }
        @Override public Invoker setShellEnvironmentInherited(boolean shellEnvironmentInherited) { return this; }
        @Override public Invoker setEnvironmentVariables(Properties environmentVariables) { return this; }
    }
}
