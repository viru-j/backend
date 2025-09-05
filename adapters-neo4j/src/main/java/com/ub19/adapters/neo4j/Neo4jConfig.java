package com.ub19.adapters.neo4j;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(Neo4jProperties.class)
public class Neo4jConfig {

    private final Neo4jProperties properties;

    public Neo4jConfig(Neo4jProperties properties) {
        this.properties = properties;
    }

    @Bean
    public Driver neo4jDriver() {
        return GraphDatabase.driver(properties.uri(),
                AuthTokens.basic(properties.user(), properties.password()));
    }
}
