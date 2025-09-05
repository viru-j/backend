package com.ub19.adapters.neo4j;

import java.util.List;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;
import org.springframework.stereotype.Service;

@Service
public class GraphService implements AutoCloseable {

    private final Driver driver;

    public GraphService(Driver driver) {
        this.driver = driver;
    }

    public List<String> findCallers(String fqn) {
        try (Session session = driver.session()) {
            return session.executeRead(tx -> tx.run(
                    "MATCH (caller:Method)-[:CALLS]->(m:Method {fqn: $fqn}) RETURN caller.fqn AS fqn",
                    Values.parameters("fqn", fqn))
                    .list(r -> r.get("fqn").asString()));
        }
    }

    public List<String> findCallees(String fqn) {
        try (Session session = driver.session()) {
            return session.executeRead(tx -> tx.run(
                    "MATCH (m:Method {fqn: $fqn})-[:CALLS]->(callee:Method) RETURN callee.fqn AS fqn",
                    Values.parameters("fqn", fqn))
                    .list(r -> r.get("fqn").asString()));
        }
    }

    public List<Endpoint> endpointsForRule(String ruleKey) {
        try (Session session = driver.session()) {
            return session.executeRead(tx -> tx.run(
                    "MATCH (r:Rule {key: $key})<-[:DEFINES_RULE]-(m:Method)<-[:HANDLED_BY]-(e:Endpoint) " +
                            "RETURN e.path AS path, e.http AS http, m.fqn AS method",
                    Values.parameters("key", ruleKey))
                    .list(r -> new Endpoint(
                            r.get("path").asString(),
                            r.get("http").asString(),
                            r.get("method").asString())));
        }
    }

    public List<String> modulesForClass(String fqn) {
        try (Session session = driver.session()) {
            return session.executeRead(tx -> tx.run(
                    "MATCH (c:Class {fqn: $fqn})-[:IN_MODULE]->(m:Module) RETURN m.name AS name",
                    Values.parameters("fqn", fqn))
                    .list(r -> r.get("name").asString()));
        }
    }

    @Override
    public void close() {
        driver.close();
    }

    public record Endpoint(String path, String http, String methodFqn) {
    }
}
