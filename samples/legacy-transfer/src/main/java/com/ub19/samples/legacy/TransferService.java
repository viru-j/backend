package com.ub19.samples.legacy;

import java.math.BigDecimal;

/**
 * Legacy service enforcing a daily transfer limit.
 */
public class TransferService {
    private static final BigDecimal DAILY_LIMIT = new BigDecimal("1000");
    private BigDecimal dailyTotal = BigDecimal.ZERO;

    /**
     * Transfers the given amount if under the daily limit.
     *
     * @throws IllegalStateException when the daily limit would be exceeded
     */
    public void transfer(BigDecimal amount) {
        if (dailyTotal.add(amount).compareTo(DAILY_LIMIT) > 0) {
            throw new IllegalStateException("Daily limit exceeded");
        }
        dailyTotal = dailyTotal.add(amount);
    }
}
