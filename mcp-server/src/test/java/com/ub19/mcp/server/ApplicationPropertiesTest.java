package com.ub19.mcp.server;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestPropertySource;

import com.ub19.adapters.neo4j.Neo4jProperties;
import com.ub19.mcp.server.lucene.LuceneProperties;

@SpringBootTest(classes = TestConfig.class)
@TestPropertySource(properties = {
        "INDEX_PATH=/tmp/index",
        "NEO4J_URI=bolt://remote:7687",
        "NEO4J_USER=alice",
        "NEO4J_PASSWORD=secret" })
class ApplicationPropertiesTest {

    @Autowired
    LuceneProperties lucene;

    @Autowired
    Neo4jProperties neo4j;

    @Test
    void envVariablesBind() {
        assertEquals("/tmp/index", lucene.getIndexPath());
        assertEquals("bolt://remote:7687", neo4j.uri());
        assertEquals("alice", neo4j.user());
        assertEquals("secret", neo4j.password());
    }
}

@Configuration
@EnableConfigurationProperties({LuceneProperties.class, Neo4jProperties.class})
class TestConfig {}

