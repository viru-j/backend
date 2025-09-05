package com.ub19.indexer;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Dummy embedder that produces a deterministic fixed-length vector based on SHA-256 hash.
 */
public class FixedEmbedder implements Embedder {

    private static final int DIM = 8;

    @Override
    public float[] embed(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            float[] vector = new float[DIM];
            for (int i = 0; i < DIM; i++) {
                vector[i] = (hash[i] & 0xff) / 255f;
            }
            return vector;
        } catch (NoSuchAlgorithmException e) {
            // Should not happen; fallback to zeros
            float[] zeros = new float[DIM];
            Arrays.fill(zeros, 0f);
            return zeros;
        }
    }
}
