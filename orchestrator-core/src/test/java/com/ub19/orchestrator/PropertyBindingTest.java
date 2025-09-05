package com.ub19.orchestrator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestPropertySource;

import com.ub19.adapters.neo4j.Neo4jProperties;

@SpringBootTest(classes = PropertyBindingTest.TestConfig.class)
@TestPropertySource(properties = {
        "NEO4J_URI=bolt://remote:7687",
        "NEO4J_USER=alice",
        "NEO4J_PASSWORD=secret" })
class PropertyBindingTest {

    @Autowired
    Neo4jProperties props;

    @Test
    void envVariablesBind() {
        assertEquals("bolt://remote:7687", props.uri());
        assertEquals("alice", props.user());
        assertEquals("secret", props.password());
    }
}

@Configuration
@EnableConfigurationProperties(Neo4jProperties.class)
class TestConfig {}

