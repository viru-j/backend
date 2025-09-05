package com.ub19.orchestrator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.ub19")
public class OrchestratorCoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrchestratorCoreApplication.class, args);
    }
}
