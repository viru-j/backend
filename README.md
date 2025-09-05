# UB-19 Backend

This project contains the multi-module build for the MCP Code Understanding & API Modernization Platform.

## Build

```bash
mvn -q -Pci -DskipTests package
```

## Run

### Orchestrator Core

```bash
java -jar orchestrator-core/target/orchestrator-core-1.0.0-SNAPSHOT.jar
```
Health endpoint: `http://localhost:8080/actuator/health`

### MCP Server

```bash
java -jar mcp-server/target/mcp-server-1.0.0-SNAPSHOT.jar
```
Health endpoint: `http://localhost:8081/actuator/health`
