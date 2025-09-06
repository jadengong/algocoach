package com.algocoach.controller;

import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/problems")
public class ProblemController {
    
    @GetMapping
    public List<Map<String, Object>> getAllProblems() {
        List<Map<String, Object>> problems = new ArrayList<>();
        
        // Easy Problems
        problems.add(createProblem(1, "Two Sum", "EASY", "Array", 45.8));
        problems.add(createProblem(2, "Valid Parentheses", "EASY", "Stack", 38.4));
        problems.add(createProblem(3, "Maximum Subarray", "EASY", "Array", 50.2));
        problems.add(createProblem(4, "Climbing Stairs", "EASY", "Dynamic Programming", 51.3));
        problems.add(createProblem(5, "Best Time to Buy and Sell Stock", "EASY", "Array", 49.7));
        
        // Medium Problems
        problems.add(createProblem(6, "Add Two Numbers", "MEDIUM", "Linked List", 36.8));
        problems.add(createProblem(7, "Longest Substring Without Repeating Characters", "MEDIUM", "Hash Table", 33.2));
        problems.add(createProblem(8, "Longest Palindromic Substring", "MEDIUM", "String", 31.5));
        problems.add(createProblem(9, "Zigzag Conversion", "MEDIUM", "String", 40.1));
        problems.add(createProblem(10, "Reverse Integer", "MEDIUM", "Math", 26.8));
        
        // Hard Problems
        problems.add(createProblem(11, "Median of Two Sorted Arrays", "HARD", "Array", 35.2));
        problems.add(createProblem(12, "Regular Expression Matching", "HARD", "String", 28.9));
        problems.add(createProblem(13, "Merge k Sorted Lists", "HARD", "Linked List", 44.7));
        problems.add(createProblem(14, "First Missing Positive", "HARD", "Array", 33.1));
        problems.add(createProblem(15, "Trapping Rain Water", "HARD", "Array", 52.3));
        
        return problems;
    }
    
    private Map<String, Object> createProblem(int id, String title, String difficulty, String topic, double acceptanceRate) {
        Map<String, Object> problem = new HashMap<>();
        problem.put("id", id);
        problem.put("title", title);
        problem.put("difficulty", difficulty);
        problem.put("topic", topic);
        problem.put("acceptanceRate", acceptanceRate);
        problem.put("leetcodeId", id);
        return problem;
    }
    
    @GetMapping("/{id}")
    public Map<String, Object> getProblemById(@PathVariable Long id) {
        List<Map<String, Object>> allProblems = getAllProblems();
        return allProblems.stream()
                .filter(problem -> problem.get("id").equals(id))
                .findFirst()
                .orElse(createProblem(0, "Problem not found", "UNKNOWN", "Unknown", 0.0));
    }
    
    @GetMapping("/difficulty/{difficulty}")
    public List<Map<String, Object>> getProblemsByDifficulty(@PathVariable String difficulty) {
        return getAllProblems().stream()
                .filter(problem -> problem.get("difficulty").toString().equalsIgnoreCase(difficulty))
                .toList();
    }
    
    @GetMapping("/topic/{topic}")
    public List<Map<String, Object>> getProblemsByTopic(@PathVariable String topic) {
        return getAllProblems().stream()
                .filter(problem -> problem.get("topic").toString().equalsIgnoreCase(topic))
                .toList();
    }
    
    @GetMapping("/search")
    public List<Map<String, Object>> searchProblems(@RequestParam(required = false) String difficulty,
                                                   @RequestParam(required = false) String topic,
                                                   @RequestParam(required = false) String title) {
        return getAllProblems().stream()
                .filter(problem -> {
                    boolean matchesDifficulty = difficulty == null || 
                            problem.get("difficulty").toString().equalsIgnoreCase(difficulty);
                    boolean matchesTopic = topic == null || 
                            problem.get("topic").toString().equalsIgnoreCase(topic);
                    boolean matchesTitle = title == null || 
                            problem.get("title").toString().toLowerCase().contains(title.toLowerCase());
                    return matchesDifficulty && matchesTopic && matchesTitle;
                })
                .toList();
    }
}
