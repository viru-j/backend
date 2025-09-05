package com.ub19.mcp.server.git;

import org.springframework.stereotype.Component;

/**
 * Default churn service returning zero for all lookups.
 */
@Component
public class NoopChurnService implements ChurnService {
    @Override
    public int churn(String fqn) {
        return 0;
    }
}
