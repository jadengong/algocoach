package com.algocoach.controller;

import com.algocoach.domain.Difficulty;
import com.algocoach.repository.ProblemRepository;
import com.algocoach.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/stats")
public class StatsController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProblemRepository problemRepository;
    
    @GetMapping("/overview")
    public Map<String, Object> getOverview() {
        Map<String, Object> stats = new HashMap<>();
        
        // User statistics
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByIsActiveTrue();
        
        // Problem statistics
        long totalProblems = problemRepository.count();
        long easyProblems = problemRepository.countByDifficulty(Difficulty.EASY);
        long mediumProblems = problemRepository.countByDifficulty(Difficulty.MEDIUM);
        long hardProblems = problemRepository.countByDifficulty(Difficulty.HARD);
        
        stats.put("users", Map.of(
            "total", totalUsers,
            "active", activeUsers,
            "inactive", totalUsers - activeUsers
        ));
        
        stats.put("problems", Map.of(
            "total", totalProblems,
            "easy", easyProblems,
            "medium", mediumProblems,
            "hard", hardProblems
        ));
        
        return stats;
    }
    
    @GetMapping("/users")
    public Map<String, Object> getUserStats() {
        Map<String, Object> userStats = new HashMap<>();
        userStats.put("totalUsers", userRepository.count());
        userStats.put("activeUsers", userRepository.countByIsActiveTrue());
        userStats.put("inactiveUsers", userRepository.countByIsActiveFalse());
        return userStats;
    }
    
    @GetMapping("/problems")
    public Map<String, Object> getProblemStats() {
        Map<String, Object> problemStats = new HashMap<>();
        problemStats.put("totalProblems", problemRepository.count());
        problemStats.put("easyProblems", problemRepository.countByDifficulty(Difficulty.EASY));
        problemStats.put("mediumProblems", problemRepository.countByDifficulty(Difficulty.MEDIUM));
        problemStats.put("hardProblems", problemRepository.countByDifficulty(Difficulty.HARD));
        return problemStats;
    }
}
