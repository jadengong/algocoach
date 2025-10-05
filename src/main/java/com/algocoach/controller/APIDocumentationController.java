package com.algocoach.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Profile;

import java.util.Map;
import java.util.HashMap;

@RestController
@Profile("dev")
@RequestMapping("/api-docs")
@CrossOrigin(origins = "*")
public class APIDocumentationController {
    
    @GetMapping("/mvp")
    public Map<String, Object> getMVPDocumentation() {
        Map<String, Object> doc = new HashMap<>();
        doc.put("title", "AlgoCoach MVP API Documentation");
        doc.put("description", "API endpoints for the AlgoCoach MVP - AI-powered technical interview preparation platform");
        doc.put("version", "1.0.0");
        doc.put("baseUrl", "http://localhost:8081");
        
        // Authentication endpoints
        Map<String, String> authEndpoints = new HashMap<>();
        authEndpoints.put("POST /auth/register", "Register a new user");
        authEndpoints.put("POST /auth/login", "Login user and get JWT token");
        authEndpoints.put("POST /auth/validate", "Validate JWT token");
        authEndpoints.put("GET /auth/health", "Check auth service health");
        
        // MVP features
        Map<String, String> mvpEndpoints = new HashMap<>();
        mvpEndpoints.put("GET /mvp/dashboard", "Get personalized dashboard with recommendations and stats");
        mvpEndpoints.put("GET /mvp/recommendations?limit=5", "Get personalized problem recommendations");
        mvpEndpoints.put("GET /mvp/problems/topic/{topic}?limit=10", "Get problems by topic (e.g., Array, Stack, DP)");
        mvpEndpoints.put("GET /mvp/problems/random?difficulty=EASY&limit=3", "Get random problems for practice");
        mvpEndpoints.put("POST /mvp/problems/{id}/start", "Start working on a problem");
        mvpEndpoints.put("POST /mvp/problems/{id}/solve?timeSpentMinutes=30", "Mark problem as solved");
        mvpEndpoints.put("POST /mvp/problems/{id}/giveup", "Give up on a problem");
        mvpEndpoints.put("POST /mvp/problems/{id}/attempt", "Record an attempt");
        mvpEndpoints.put("POST /mvp/problems/{id}/hint", "Use a hint");
        mvpEndpoints.put("GET /mvp/stats", "Get user progress statistics");
        mvpEndpoints.put("GET /mvp/progress", "Get all user progress");
        mvpEndpoints.put("GET /mvp/progress/solved", "Get solved problems");
        mvpEndpoints.put("GET /mvp/progress/in-progress", "Get in-progress problems");
        mvpEndpoints.put("GET /mvp/problems/{id}/progress", "Get progress for specific problem");
        
        // Problem management
        Map<String, String> problemEndpoints = new HashMap<>();
        problemEndpoints.put("GET /problems", "Get all problems");
        problemEndpoints.put("GET /problems/{id}", "Get problem by ID");
        problemEndpoints.put("GET /problems/difficulty/{difficulty}", "Filter by difficulty (EASY, MEDIUM, HARD)");
        problemEndpoints.put("GET /problems/topic/{topic}", "Filter by topic");
        problemEndpoints.put("GET /problems/search?difficulty=EASY&topic=Array&title=sum", "Advanced search");
        problemEndpoints.put("POST /problems", "Create new problem (admin)");
        problemEndpoints.put("PUT /problems/{id}", "Update problem (admin)");
        problemEndpoints.put("DELETE /problems/{id}", "Delete problem (admin)");
        
        Map<String, Object> endpoints = new HashMap<>();
        endpoints.put("authentication", authEndpoints);
        endpoints.put("mvp_features", mvpEndpoints);
        endpoints.put("problem_management", problemEndpoints);
        
        // Sample workflow
        Map<String, String> workflow = new HashMap<>();
        workflow.put("step1", "Register/Login to get JWT token");
        workflow.put("step2", "Get recommendations: GET /mvp/recommendations");
        workflow.put("step3", "Start a problem: POST /mvp/problems/{id}/start");
        workflow.put("step4", "Record attempts: POST /mvp/problems/{id}/attempt");
        workflow.put("step5", "Use hints if needed: POST /mvp/problems/{id}/hint");
        workflow.put("step6", "Mark as solved: POST /mvp/problems/{id}/solve");
        workflow.put("step7", "Check progress: GET /mvp/stats");
        
        // Authentication headers
        Map<String, String> authHeaders = new HashMap<>();
        authHeaders.put("note", "All MVP endpoints require authentication");
        authHeaders.put("header", "Authorization: Bearer {jwt_token}");
        authHeaders.put("example", "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...");
        
        doc.put("endpoints", endpoints);
        doc.put("sample_workflow", workflow);
        doc.put("authentication_headers", authHeaders);
        doc.put("difficulty_levels", new String[]{"EASY", "MEDIUM", "HARD"});
        doc.put("topics", new String[]{"Array", "Stack", "Dynamic Programming", "Hash Table", "String", "Linked List", "Math"});
        doc.put("progress_statuses", new String[]{"NOT_STARTED", "IN_PROGRESS", "SOLVED", "GAVE_UP"});
        
        return doc;
    }
    
    @GetMapping("/test-data")
    public Map<String, Object> getTestData() {
        Map<String, Object> testData = new HashMap<>();
        
        Map<String, String> userRegistration = new HashMap<>();
        userRegistration.put("username", "testuser");
        userRegistration.put("email", "test@example.com");
        userRegistration.put("password", "password123");
        userRegistration.put("firstName", "Test");
        userRegistration.put("lastName", "User");
        
        Map<String, String> login = new HashMap<>();
        login.put("username", "testuser");
        login.put("password", "password123");
        
        testData.put("sample_user_registration", userRegistration);
        testData.put("sample_login", login);
        testData.put("sample_problem_ids", new Long[]{1L, 2L, 3L, 4L, 5L, 6L, 7L, 11L});
        testData.put("sample_topics", new String[]{"Array", "Stack", "Dynamic Programming", "Hash Table", "String", "Linked List"});
        testData.put("h2_console", "http://localhost:8081/h2-console (username: sa, password: password)");
        
        return testData;
    }

    @GetMapping("/ping")
    public Map<String, Object> ping() {
        Map<String, Object> res = new HashMap<>();
        res.put("status", "ok");
        res.put("service", "api-docs");
        res.put("time", System.currentTimeMillis());
        return res;
    }
}
