package com.ub19.mcp.server.git;

/**
 * Provides simple git churn metrics.
 */
public interface ChurnService {
    /**
     * Returns the number of commits touching the given fully qualified name.
     */
    int churn(String fqn);
}
