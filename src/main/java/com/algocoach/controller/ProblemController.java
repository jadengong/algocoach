package com.algocoach.controller;

import com.algocoach.domain.Difficulty;
import com.algocoach.domain.Problem;
import com.algocoach.repository.ProblemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/problems")
public class ProblemController {
    
    @Autowired
    private ProblemRepository problemRepository;
    
    @GetMapping
    public List<Problem> getAllProblems() {
        return problemRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public Problem getProblemById(@PathVariable Long id) {
        return problemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Problem not found with id: " + id));
    }
    
    @GetMapping("/difficulty/{difficulty}")
    public List<Problem> getProblemsByDifficulty(@PathVariable String difficulty) {
        try {
            Difficulty difficultyEnum = Difficulty.valueOf(difficulty.toUpperCase());
            return problemRepository.findByDifficulty(difficultyEnum);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid difficulty level: " + difficulty);
        }
    }
    
    @GetMapping("/topic/{topic}")
    public List<Problem> getProblemsByTopic(@PathVariable String topic) {
        return problemRepository.findByTopicIgnoreCase(topic);
    }
    
    @GetMapping("/search")
    public List<Problem> searchProblems(@RequestParam(required = false) String difficulty,
                                       @RequestParam(required = false) String topic,
                                       @RequestParam(required = false) String title) {
        Difficulty difficultyEnum = null;
        if (difficulty != null) {
            try {
                difficultyEnum = Difficulty.valueOf(difficulty.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid difficulty level: " + difficulty);
            }
        }
        return problemRepository.findByFilters(difficultyEnum, topic, title);
    }
    
    @PostMapping
    public Problem createProblem(@RequestBody Problem problem) {
        return problemRepository.save(problem);
    }
    
    @PutMapping("/{id}")
    public Problem updateProblem(@PathVariable Long id, @RequestBody Problem problemDetails) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Problem not found with id: " + id));
        
        problem.setTitle(problemDetails.getTitle());
        problem.setDifficulty(problemDetails.getDifficulty());
        problem.setTopic(problemDetails.getTopic());
        problem.setAcceptanceRate(problemDetails.getAcceptanceRate());
        problem.setLeetcodeId(problemDetails.getLeetcodeId());
        problem.setDescription(problemDetails.getDescription());
        problem.setExamples(problemDetails.getExamples());
        problem.setConstraints(problemDetails.getConstraints());
        
        return problemRepository.save(problem);
    }
    
    @DeleteMapping("/{id}")
    public void deleteProblem(@PathVariable Long id) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Problem not found with id: " + id));
        problemRepository.delete(problem);
    }
}