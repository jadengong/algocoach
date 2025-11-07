package com.algocoach.controller;

import com.algocoach.domain.Difficulty;
import com.algocoach.domain.Problem;
import com.algocoach.exception.ResourceNotFoundException;
import com.algocoach.exception.ValidationException;
import com.algocoach.repository.ProblemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
                .orElseThrow(() -> new ResourceNotFoundException("Problem", id.toString()));
    }
    
    @GetMapping("/difficulty/{difficulty}")
    public List<Problem> getProblemsByDifficulty(@PathVariable String difficulty) {
        try {
            Difficulty difficultyEnum = Difficulty.valueOf(difficulty.toUpperCase());
            return problemRepository.findByDifficulty(difficultyEnum);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid difficulty level: " + difficulty);
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
                throw new ValidationException("Invalid difficulty level: " + difficulty);
            }
        }
        return problemRepository.findByFilters(difficultyEnum, topic, title);
    }
    
    @GetMapping("/discover")
    public Map<String, Object> discoverProblems(
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String topic,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Difficulty difficultyEnum = null;
        if (difficulty != null) {
            try {
                difficultyEnum = Difficulty.valueOf(difficulty.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ValidationException("Invalid difficulty level: " + difficulty);
            }
        }
        
        List<Problem> problems = problemRepository.findByFilters(difficultyEnum, topic, null);
        
        // Apply sorting
        if (sortBy != null) {
            switch (sortBy.toLowerCase()) {
                case "difficulty":
                    problems.sort((p1, p2) -> p1.getDifficulty().compareTo(p2.getDifficulty()));
                    break;
                case "acceptance":
                    problems.sort((p1, p2) -> Double.compare(p2.getAcceptanceRate(), p1.getAcceptanceRate()));
                    break;
                case "title":
                    problems.sort((p1, p2) -> p1.getTitle().compareToIgnoreCase(p2.getTitle()));
                    break;
                default:
                    // Default sort by acceptance rate (easiest first)
                    problems.sort((p1, p2) -> Double.compare(p2.getAcceptanceRate(), p1.getAcceptanceRate()));
            }
        }
        
        // Apply pagination
        int start = page * size;
        int end = Math.min(start + size, problems.size());
        List<Problem> paginatedProblems = problems.subList(start, end);
        
        Map<String, Object> result = new HashMap<>();
        result.put("problems", paginatedProblems);
        result.put("totalCount", problems.size());
        result.put("page", page);
        result.put("size", size);
        result.put("totalPages", (int) Math.ceil((double) problems.size() / size));
        result.put("hasNext", end < problems.size());
        result.put("hasPrevious", page > 0);
        
        return result;
    }
    
    @PostMapping
    public Problem createProblem(@RequestBody Problem problem) {
        return problemRepository.save(problem);
    }
    
    @PutMapping("/{id}")
    public Problem updateProblem(@PathVariable Long id, @RequestBody Problem problemDetails) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Problem", id.toString()));
        
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
                .orElseThrow(() -> new ResourceNotFoundException("Problem", id.toString()));
        problemRepository.delete(problem);
    }
    
    @GetMapping("/filters")
    public Map<String, Object> getAvailableFilters() {
        Map<String, Object> filters = new HashMap<>();
        
        // Get all unique topics
        List<String> topics = problemRepository.findAll().stream()
                .map(Problem::getTopic)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        
        // Get all difficulties
        List<String> difficulties = Arrays.stream(Difficulty.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        
        filters.put("topics", topics);
        filters.put("difficulties", difficulties);
        filters.put("sortOptions", Arrays.asList("difficulty", "acceptance", "title"));
        
        return filters;
    }
}