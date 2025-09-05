package com.ub19.quality;

import java.nio.file.Path;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.boot.ExitCodeGenerator;

@Component
public class QualityRunnerRunner implements ApplicationRunner, ExitCodeGenerator {

    private final QualityService service;
    private int exitCode = 0;
    private static final Pattern SAFE_MODULE = Pattern.compile("[A-Za-z0-9_-]+");

    public QualityRunnerRunner(QualityService service) {
        this.service = service;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!args.getNonOptionArgs().isEmpty() ||
                !args.getOptionNames().stream().allMatch(Set.of("module")::contains)) {
            System.err.println("Unknown arguments");
            exitCode = 1;
            return;
        }
        String module = args.getOptionValues("module") != null && !args.getOptionValues("module").isEmpty()
                ? args.getOptionValues("module").get(0)
                : null;
        if (module == null || !SAFE_MODULE.matcher(module).matches()) {
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
