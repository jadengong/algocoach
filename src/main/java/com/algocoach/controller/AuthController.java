package com.algocoach.controller;

import com.algocoach.annotation.RateLimited;
import com.algocoach.dto.AuthResponse;
import com.algocoach.dto.LoginRequest;
import com.algocoach.dto.RegisterRequest;
import com.algocoach.exception.AuthenticationException;
import com.algocoach.exception.BusinessLogicException;
import com.algocoach.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    @RateLimited(value = 5) // 5 registrations per minute
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        AuthResponse response = authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @RateLimited(value = 10) // 10 login attempts per minute
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/validate")
    @RateLimited(value = 100) // 100 token validations per minute
    public ResponseEntity<Map<String, Object>> validateToken(@RequestHeader("Authorization") String token) {
        // Remove "Bearer " prefix if present
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        // Validate token
        if (authService.validateToken(token)) {
            return ResponseEntity.ok(Map.of("valid", true));
        } else {
            throw new AuthenticationException("Invalid token");
        }
    }

    @GetMapping("/health")
    @RateLimited(value = 200) // 200 health checks per minute
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of("status", "Auth service is running"));
    }
}
