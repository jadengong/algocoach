package com.algocoach.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RateLimitingConfigTest {

    private RateLimitingConfig rateLimitingConfig;

    @BeforeEach
    void setUp() {
        rateLimitingConfig = new RateLimitingConfig();
    }

    @Test
    void allowsRequestsWithinLimit() {
        String key = "client-1";
        int limit = 3;

        assertTrue(rateLimitingConfig.isAllowed(key, limit));
        assertTrue(rateLimitingConfig.isAllowed(key, limit));
        assertTrue(rateLimitingConfig.isAllowed(key, limit));

        // Next one should be blocked
        assertFalse(rateLimitingConfig.isAllowed(key, limit));
    }

    @Test
    void separateKeysAreTrackedIndependently() {
        int limit = 1;
        assertTrue(rateLimitingConfig.isAllowed("client-a", limit));
        assertFalse(rateLimitingConfig.isAllowed("client-a", limit));

        // Different key should still be allowed
        assertTrue(rateLimitingConfig.isAllowed("client-b", limit));
    }
}


