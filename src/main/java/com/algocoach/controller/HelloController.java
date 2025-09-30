package com.algocoach.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello from AlgoCoach!";
    }
    
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("service", "AlgoCoach API");
        return health;
    }
    
    @GetMapping("/health/detailed")
    public Map<String, Object> detailedHealth() {
        Map<String, Object> health = new HashMap<>();
        
        // Basic health status
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        
        // Application info
        health.put("application", Map.of(
            "name", "AlgoCoach",
            "version", "1.0.0",
            "environment", "development"
        ));
        
        // System info
        Runtime runtime = Runtime.getRuntime();
        health.put("system", Map.of(
            "javaVersion", System.getProperty("java.version"),
            "totalMemory", runtime.totalMemory(),
            "freeMemory", runtime.freeMemory(),
            "maxMemory", runtime.maxMemory()
        ));
        
        return health;
    }
}
