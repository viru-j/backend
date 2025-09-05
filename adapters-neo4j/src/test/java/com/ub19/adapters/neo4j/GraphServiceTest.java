package com.ub19.adapters.neo4j;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


@Testcontainers
class GraphServiceTest {

    @Container
    static Neo4jContainer<?> neo4j = new Neo4jContainer<>("neo4j:5.19.0");

    private GraphService service;

    @BeforeEach
    void setUp() {
        Driver driver = GraphDatabase.driver(neo4j.getBoltUrl(),
                AuthTokens.basic("neo4j", neo4j.getAdminPassword()));
        service = new GraphService(driver);
        try (Session session = driver.session()) {
            session.run("CREATE (a:Method {fqn:'A'})");
            session.run("CREATE (b:Method {fqn:'B'})");
            session.run("CREATE (c:Method {fqn:'C'})");
            session.run("MATCH (a:Method {fqn:'A'}), (b:Method {fqn:'B'}) CREATE (b)-[:CALLS]->(a)");
            session.run("MATCH (a:Method {fqn:'A'}), (c:Method {fqn:'C'}) CREATE (a)-[:CALLS]->(c)");
            session.run("MATCH (a:Method {fqn:'A'}) CREATE (r:Rule {key:'R1'})<-[:DEFINES_RULE]-(a)");
            session.run("MATCH (a:Method {fqn:'A'}) CREATE (e:Endpoint {path:'/test', http:'GET'})-[:HANDLED_BY]->(a)");
            session.run("CREATE (:Class {fqn:'com.example.A'})-[:IN_MODULE]->(:Module {name:'core'})");
        }
    }

    @AfterEach
    void tearDown() throws Exception {
        service.close();
    }

    @Test
    void findCallers() {
        assertEquals(List.of("B"), service.findCallers("A"));
    }

    @Test
    void findCallees() {
        assertEquals(List.of("C"), service.findCallees("A"));
    }

    @Test
    void endpointsForRule() {
        List<GraphService.Endpoint> eps = service.endpointsForRule("R1");
        assertEquals(1, eps.size());
        GraphService.Endpoint ep = eps.get(0);
        assertEquals("/test", ep.path());
        assertEquals("GET", ep.http());
        assertEquals("A", ep.methodFqn());
    }

    @Test
    void modulesForClass() {
        assertEquals(List.of("core"), service.modulesForClass("com.example.A"));
    }
}
