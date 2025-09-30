package com.algocoach.controller;

import com.algocoach.domain.Difficulty;
import com.algocoach.domain.Problem;
import com.algocoach.repository.ProblemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/stats")
public class StatsController {
    
    @Autowired
    private ProblemRepository problemRepository;
    
    @GetMapping("/overview")
    public Map<String, Object> getOverview() {
        Map<String, Object> stats = new HashMap<>();
        
        // Problem statistics
        long totalProblems = problemRepository.count();
        long easyProblems = problemRepository.countByDifficulty(Difficulty.EASY);
        long mediumProblems = problemRepository.countByDifficulty(Difficulty.MEDIUM);
        long hardProblems = problemRepository.countByDifficulty(Difficulty.HARD);
        
        stats.put("problems", Map.of(
            "total", totalProblems,
            "easy", easyProblems,
            "medium", mediumProblems,
            "hard", hardProblems
        ));
        
        return stats;
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
    
    @GetMapping("/difficulty-breakdown")
    public Map<String, Object> getDifficultyBreakdown() {
        Map<String, Object> breakdown = new HashMap<>();
        
        long totalProblems = problemRepository.count();
        long easyProblems = problemRepository.countByDifficulty(Difficulty.EASY);
        long mediumProblems = problemRepository.countByDifficulty(Difficulty.MEDIUM);
        long hardProblems = problemRepository.countByDifficulty(Difficulty.HARD);
        
        breakdown.put("total", totalProblems);
        breakdown.put("breakdown", Map.of(
            "easy", Map.of(
                "count", easyProblems,
                "percentage", totalProblems > 0 ? Math.round((double) easyProblems / totalProblems * 100) : 0
            ),
            "medium", Map.of(
                "count", mediumProblems,
                "percentage", totalProblems > 0 ? Math.round((double) mediumProblems / totalProblems * 100) : 0
            ),
            "hard", Map.of(
                "count", hardProblems,
                "percentage", totalProblems > 0 ? Math.round((double) hardProblems / totalProblems * 100) : 0
            )
        ));
        
        // Calculate average acceptance rate by difficulty
        Map<String, Double> avgAcceptanceByDifficulty = new HashMap<>();
        for (Difficulty difficulty : Difficulty.values()) {
            List<Problem> problems = problemRepository.findByDifficulty(difficulty);
            if (!problems.isEmpty()) {
                double avgAcceptance = problems.stream()
                        .mapToDouble(Problem::getAcceptanceRate)
                        .average()
                        .orElse(0.0);
                avgAcceptanceByDifficulty.put(difficulty.name().toLowerCase(), 
                    Math.round(avgAcceptance * 100.0) / 100.0);
            }
        }
        breakdown.put("averageAcceptanceRates", avgAcceptanceByDifficulty);
        
        return breakdown;
    }
    
    @GetMapping("/topic-breakdown")
    public Map<String, Object> getTopicBreakdown() {
        Map<String, Object> topicStats = new HashMap<>();
        
        List<Problem> allProblems = problemRepository.findAll();
        Map<String, Long> topicCounts = allProblems.stream()
                .collect(Collectors.groupingBy(Problem::getTopic, Collectors.counting()));
        
        long totalProblems = allProblems.size();
        
        Map<String, Object> breakdown = new HashMap<>();
        for (Map.Entry<String, Long> entry : topicCounts.entrySet()) {
            String topic = entry.getKey();
            Long count = entry.getValue();
            int percentage = totalProblems > 0 ? (int) Math.round((double) count / totalProblems * 100) : 0;
            
            breakdown.put(topic, Map.of(
                "count", count,
                "percentage", percentage
            ));
        }
        
        topicStats.put("total", totalProblems);
        topicStats.put("breakdown", breakdown);
        
        return topicStats;
    }
}
