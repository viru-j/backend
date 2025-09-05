package com.ub19.quality;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.boot.DefaultApplicationArguments;
import com.fasterxml.jackson.databind.ObjectMapper;

class QualityRunnerRunnerTest {

    @Test
    void rejectsUnknownOption() {
        QualityService svc = new QualityService(null, new ObjectMapper()) {
            @Override public int run(java.nio.file.Path modulePath) { return 0; }
        };
        QualityRunnerRunner runner = new QualityRunnerRunner(svc);
        runner.run(new DefaultApplicationArguments("--bad=1"));
        assertEquals(1, runner.getExitCode());
    }

    @Test
    void rejectsInvalidModule() {
        QualityService svc = new QualityService(null, new ObjectMapper()) {
            @Override public int run(java.nio.file.Path modulePath) { return 0; }
        };
        QualityRunnerRunner runner = new QualityRunnerRunner(svc);
        runner.run(new DefaultApplicationArguments("--module=../etc"));
        assertEquals(1, runner.getExitCode());
    }
}

