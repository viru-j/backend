package com.ub19.orchestrator.audit;

import static net.logstash.logback.argument.StructuredArguments.kv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Emits structured JSON logs for tool invocations.
 */
@Service
public class AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);

    /**
     * Records a tool invocation with predefined fields.
     *
     * @param uc use case identifier
     * @param tool tool name
     * @param durationMs execution duration in milliseconds
     * @param hits number of hits returned
     */
    public void record(String uc, String tool, long durationMs, int hits) {
        log.info("tool-call",
                kv("actor", "system"),
                kv("uc", uc),
                kv("tool", tool),
                kv("duration_ms", durationMs),
                kv("hits", hits));
    }
}

