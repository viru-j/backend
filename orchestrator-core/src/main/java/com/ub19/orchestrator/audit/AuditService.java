package com.ub19.orchestrator.audit;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Emits JSON audit logs for tool invocations.
 */
@Service
public class AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);
    private final ObjectMapper mapper;

    public AuditService(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public void record(String tool, Object request, Object response) {
        Map<String, Object> entry = Map.of(
                "tool", tool,
                "request", request,
                "response", response);
        try {
            log.info(mapper.writeValueAsString(entry));
        } catch (JsonProcessingException e) {
            log.warn("audit-log-failed", e);
        }
    }
}
