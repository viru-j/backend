package com.ub19.mcp.server.embed;

/**
 * Simple embedding interface to allow pluggable implementations.
 */
public interface Embedder {
    float[] embed(String text);
}

