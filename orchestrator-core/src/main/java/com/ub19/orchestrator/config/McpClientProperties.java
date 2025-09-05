package com.ub19.orchestrator.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for MCP server access.
 */
@ConfigurationProperties(prefix = "mcp")
public class McpClientProperties {

    /** Base URL of the MCP server. */
    private String baseUrl;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
