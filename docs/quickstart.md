# Quickstart

This guide shows how to index the sample legacy project, run the MCP server, and call tools.

## Prerequisites
- Java 17+
- Maven 3.9+
- Neo4j running locally (default `bolt://localhost:7687`)

## Build the project
```bash
mvn -q -Pci -DskipTests package
```

## Index the sample repository
The sample project **legacy-transfer** demonstrates a daily transfer limit rule.
Run the helper script to index it into Lucene and seed Neo4j.
```bash
./samples/legacy-transfer/index.sh
```

## Start the MCP server
```bash
java -jar mcp-server/target/mcp-server-1.0.0-SNAPSHOT.jar
```
Health endpoint: <http://localhost:8081/actuator/health>

## Search for the daily limit rule
With the server running, query the `search_code` tool for "daily limit".
```bash
curl -s -X POST localhost:8081/tools/search_code \
  -H 'Content-Type: application/json' \
  -d '{"query":"daily limit","topK":5}'
```
The response contains matching code snippets including the `TransferService` rule.

## Next steps
- Explore other tools such as `explain_code` or `impact_analysis`.
- See [API documentation](api.md) for full endpoint details.
