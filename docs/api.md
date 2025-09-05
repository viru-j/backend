# API Reference

All endpoints are exposed by the MCP server on port `8081`.
Requests and responses use JSON.

## /tools/search_code
**POST** `{query, topK}` → `{hits:[CodeHit]}`
```json
// Request
{
  "query": "daily limit",
  "topK": 5
}
```
`CodeHit`:
```json
{
  "filePath": "samples/legacy-transfer/.../TransferService.java",
  "lineStart": 10,
  "lineEnd": 20,
  "snippet": "code...",
  "score": 1.23
}
```

## /tools/explain_code
**POST** `{question, topK}` → `{explanationMd, citations[]}`

## /tools/graph_query
**POST** `{templateKey, params}` → `{rows[]}`
Templates include `callers`, `callees`, `modulesForClass`, `endpointsForRule`.

## /tools/impact_analysis
**POST** `{targetFqn, depth, includeEndpoints}` →
`{callers[], callees[], modules[], endpoints[], riskMd}`

## /tools/openapi_to_skeleton
**POST** `{openapiYaml}` → `{checklist[], packages{controller,service,repository,dto}}`

## /tools/generate_api
**POST** `{openapiYaml|storyMd, packageBase}` → `{workspace, files[]}`
Generates a temporary Maven project with controller/service/repo/DTO/tests.

## /tools/run_tests
**POST** `{module}` → `QualityReport`

## /tools/static_scan
**POST** `{module}` → `QualityReport`

## /tools/quality_report
**POST** `{module, branch}` → `QualityReport`

`QualityReport`:
```json
{
  "module": "mcp-server",
  "surefire": [{"path":"target/surefire-reports/TEST-*.xml","blocker":false}],
  "failsafe": [],
  "pmd": [],
  "spotbugs": [],
  "semgrep": []
}
```

## Error format
Errors return RFC‑7807 `ProblemDetail` JSON with `type`, `title`, and `detail` fields.
