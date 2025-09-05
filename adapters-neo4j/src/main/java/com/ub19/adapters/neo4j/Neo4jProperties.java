package com.ub19.adapters.neo4j;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "neo4j")
public record Neo4jProperties(String uri, String user, String password) {
}
