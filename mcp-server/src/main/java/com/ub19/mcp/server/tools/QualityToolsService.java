package com.ub19.mcp.server.tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ub19.shared.model.dto.QualityReport;
import com.ub19.shared.model.error.ApiError;
import com.ub19.shared.model.error.ApiException;

/**
 * Executes quality checks by delegating to the quality-runner CLI.
 */
@Service
public class QualityToolsService {

    private static final Pattern SAFE_MODULE = Pattern.compile("[A-Za-z0-9_-]+" );

    private final ObjectMapper mapper;

    public QualityToolsService(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public QualityReport runTests(String module) {
        return runRunner(module);
    }

    public QualityReport staticScan(String module) {
        return runRunner(module);
    }

    public QualityReport qualityReport(String module, String branch) {
        return runRunner(module);
    }

    private QualityReport runRunner(String module) {
        if (!SAFE_MODULE.matcher(module).matches()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, new ApiError("INVALID_MODULE", "Invalid module"));
        }
        Path jar = Path.of("quality-runner/target/quality-runner-1.0.0-SNAPSHOT.jar");
        if (!Files.exists(jar)) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, new ApiError("RUNNER_MISSING", "quality-runner jar not found"));
        }
        ProcessBuilder pb = new ProcessBuilder("java", "-jar", jar.toString(), "--module=" + module);
        pb.redirectErrorStream(true);
        try {
            Process proc = pb.start();
            int code = proc.waitFor();
            if (code != 0) {
                throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, new ApiError("RUNNER_FAILED", "quality-runner exited with " + code));
            }
            Path reportPath = Path.of(module, "quality-report.json");
            return mapper.readValue(reportPath.toFile(), QualityReport.class);
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, new ApiError("RUNNER_ERROR", e.getMessage()));
        }
    }
}
