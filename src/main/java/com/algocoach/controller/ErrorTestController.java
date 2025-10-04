package com.algocoach.controller;

import com.algocoach.annotation.RateLimited;
import com.algocoach.exception.AuthenticationException;
import com.algocoach.exception.BusinessLogicException;
import com.algocoach.exception.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

import java.util.Map;

@RestController
@RequestMapping("/error-test")
@CrossOrigin(origins = "*")
public class ErrorTestController {

    /**
     * Test validation error handling
     */
    @PostMapping("/validation")
    @RateLimited(value = 10) // 10 validation tests per minute
    public ResponseEntity<Map<String, Object>> testValidation(
            @Valid @RequestBody TestRequest request) {
        return ResponseEntity.ok(Map.of("message", "Validation passed", "data", request));
    }

    /**
     * Test resource not found error
     */
    @GetMapping("/not-found/{id}")
    public ResponseEntity<Map<String, Object>> testNotFound(@PathVariable Long id) {
        throw new ResourceNotFoundException("TestResource", id.toString());
    }

    /**
     * Test business logic error
     */
    @PostMapping("/business-logic")
    public ResponseEntity<Map<String, Object>> testBusinessLogic(@RequestBody Map<String, String> data) {
        if (!data.containsKey("requiredField")) {
            throw new BusinessLogicException("Required field is missing");
        }
        return ResponseEntity.ok(Map.of("message", "Business logic passed"));
    }

    /**
     * Test authentication error
     */
    @GetMapping("/auth-error")
    public ResponseEntity<Map<String, Object>> testAuthError() {
        throw new AuthenticationException("Invalid authentication token");
    }

    /**
     * Test validation error with invalid parameters
     */
    @GetMapping("/param-validation")
    public ResponseEntity<Map<String, Object>> testParamValidation(
            @RequestParam @Min(1) @Max(100) int value,
            @RequestParam @NotBlank String name) {
        return ResponseEntity.ok(Map.of("message", "Parameter validation passed", "value", value, "name", name));
    }

    /**
     * Test rate limiting (try calling this endpoint multiple times quickly)
     */
    @GetMapping("/rate-limit")
    @RateLimited(value = 2) // Only 2 requests per minute for testing
    public ResponseEntity<Map<String, Object>> testRateLimit() {
        return ResponseEntity.ok(Map.of("message", "Rate limit test passed"));
    }

    /**
     * Test internal server error
     */
    @GetMapping("/server-error")
    public ResponseEntity<Map<String, Object>> testServerError() {
        throw new RuntimeException("Simulated server error");
    }

    // Test request DTO for validation
    public static class TestRequest {
        @NotBlank(message = "Name is required")
        private String name;

        @Min(value = 1, message = "Age must be at least 1")
        @Max(value = 120, message = "Age must be at most 120")
        private Integer age;

        // Getters and Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }
    }
}
