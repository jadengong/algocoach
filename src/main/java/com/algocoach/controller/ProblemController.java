package com.algocoach.controller;

import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/problems")
public class ProblemController {
    
    @GetMapping
    public List<Map<String, Object>> getAllProblems() {
        List<Map<String, Object>> problems = new ArrayList<>();
        
        Map<String, Object> problem1 = new HashMap<>();
        problem1.put("id", 1);
        problem1.put("title", "Two Sum");
        problem1.put("difficulty", "EASY");
        problem1.put("topic", "Array");
        problems.add(problem1);
        
        Map<String, Object> problem2 = new HashMap<>();
        problem2.put("id", 2);
        problem2.put("title", "Add Two Numbers");
        problem2.put("difficulty", "MEDIUM");
        problem2.put("topic", "Linked List");
        problems.add(problem2);
        
        return problems;
    }
    
    @GetMapping("/{id}")
    public Map<String, Object> getProblemById(@PathVariable Long id) {
        Map<String, Object> problem = new HashMap<>();
        problem.put("id", id);
        problem.put("title", "Sample Problem " + id);
        problem.put("difficulty", "EASY");
        problem.put("topic", "Array");
        return problem;
    }
}
