package com.ub19.quality;

import java.nio.file.Path;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.boot.ExitCodeGenerator;

@Component
public class QualityRunnerRunner implements ApplicationRunner, ExitCodeGenerator {

    private final QualityService service;
    private int exitCode = 0;

    public QualityRunnerRunner(QualityService service) {
        this.service = service;
    }

    @Override
    public void run(ApplicationArguments args) {
        String module = args.getOptionValues("module") != null && !args.getOptionValues("module").isEmpty()
                ? args.getOptionValues("module").get(0)
                : null;
        if (module == null) {
            System.err.println("--module option required");
            exitCode = 1;
            return;
        }
        exitCode = service.run(Path.of(module).toAbsolutePath());
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }
}
