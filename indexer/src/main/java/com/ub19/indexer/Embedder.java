package com.ub19.indexer;

/**
 * Simple embedding interface to allow pluggable implementations.
 */
public interface Embedder {
    float[] embed(String text);
}
