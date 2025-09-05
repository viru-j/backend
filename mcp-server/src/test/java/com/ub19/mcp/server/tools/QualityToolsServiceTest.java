package com.ub19.mcp.server.tools;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ub19.shared.model.error.ApiException;

class QualityToolsServiceTest {

    @Test
    void runRunnerTimesOut() throws Exception {
        Files.createDirectories(Path.of("quality-runner/target"));
        Files.writeString(Path.of("quality-runner/target/quality-runner-1.0.0-SNAPSHOT.jar"), "jar");
        QualityToolsService svc = new QualityToolsService(new ObjectMapper()) {
            @Override
            protected Process start(ProcessBuilder pb) {
                return new HangingProcess();
            }
        };
        ApiException ex = assertThrows(ApiException.class, () -> svc.runTests("mod"));
        assertEquals("RUNNER_TIMEOUT", ex.getError().code());
    }

    static class HangingProcess extends Process {
        @Override
        public OutputStream getOutputStream() { return OutputStream.nullOutputStream(); }
        @Override
        public InputStream getInputStream() { return InputStream.nullInputStream(); }
        @Override
        public InputStream getErrorStream() { return InputStream.nullInputStream(); }
        @Override
        public int waitFor() { return 0; }
        @Override
        public boolean waitFor(long timeout, TimeUnit unit) { return false; }
        @Override
        public int exitValue() { return 0; }
        @Override
        public void destroy() { }
        @Override
        public Process destroyForcibly() { return this; }
        @Override
        public boolean isAlive() { return true; }
    }
}

