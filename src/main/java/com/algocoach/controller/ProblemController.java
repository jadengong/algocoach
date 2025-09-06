package com.algocoach.controller;

import com.algocoach.domain.Problem;
import com.algocoach.domain.Difficulty;
import com.algocoach.repository.ProblemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
    public ResponseEntity<Problem> getProblemById(@PathVariable Long id) {
        Optional<Problem> problem = problemRepository.findById(id);
        return problem.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/difficulty/{difficulty}")
    public List<Problem> getProblemsByDifficulty(@PathVariable Difficulty difficulty) {
        return problemRepository.findByDifficulty(difficulty);
    }
    
    @GetMapping("/topic/{topic}")
    public List<Problem> getProblemsByTopic(@PathVariable String topic) {
        return problemRepository.findByTopic(topic);
    }
    
    @PostMapping
    public Problem createProblem(@RequestBody Problem problem) {
        return problemRepository.save(problem);
    }
}
