package com.algocoach.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class RateLimitingConfig {

    private final Map<String, RateLimitInfo> cache = new ConcurrentHashMap<>();

    public boolean isAllowed(String key, int requestsPerMinute) {
        long currentTime = System.currentTimeMillis();
        RateLimitInfo info = cache.computeIfAbsent(key, k -> new RateLimitInfo());
        
        // Reset counter if a minute has passed
        if (currentTime - info.getWindowStart() >= 60000) {
            info.setCount(0);
            info.setWindowStart(currentTime);
        }
        
        // Check if limit exceeded
        if (info.getCount() >= requestsPerMinute) {
            return false;
        }
        
        // Increment counter
        info.incrementCount();
        return true;
    }


    private static class RateLimitInfo {
        private int count = 0;
        private long windowStart = System.currentTimeMillis();

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public void incrementCount() {
            this.count++;
        }

        public long getWindowStart() {
            return windowStart;
        }

        public void setWindowStart(long windowStart) {
            this.windowStart = windowStart;
        }
    }
}
