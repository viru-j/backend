package com.ub19.orchestrator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.ub19.orchestrator.config.McpClientProperties;

@SpringBootApplication(scanBasePackages = "com.ub19")
@EnableConfigurationProperties(McpClientProperties.class)
public class OrchestratorCoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrchestratorCoreApplication.class, args);
    }
}
