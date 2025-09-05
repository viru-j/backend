#!/usr/bin/env bash
set -euo pipefail
SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
INDEX_DIR=${1:-$SCRIPT_DIR/../../data/index}
NEO4J_URI=${NEO4J_URI:-bolt://localhost:7687}
NEO4J_USER=${NEO4J_USER:-neo4j}
NEO4J_PASSWORD=${NEO4J_PASSWORD:-password}

# run indexer
java -jar "$SCRIPT_DIR/../../indexer/target/indexer-1.0.0-SNAPSHOT.jar" --repo="$SCRIPT_DIR" --index="$INDEX_DIR"

# seed Neo4j with basic class node
cypher-shell -a "$NEO4J_URI" -u "$NEO4J_USER" -p "$NEO4J_PASSWORD" "MERGE (:Class {fqn:'com.ub19.samples.legacy.TransferService', file_path:'samples/legacy-transfer/src/main/java/com/ub19/samples/legacy/TransferService.java'})"
