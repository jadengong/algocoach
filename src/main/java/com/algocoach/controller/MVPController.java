package com.algocoach.controller;

import com.algocoach.domain.Difficulty;
import com.algocoach.domain.Problem;
import com.algocoach.domain.User;
import com.algocoach.domain.UserProgress;
import com.algocoach.repository.ProblemRepository;
import com.algocoach.service.ProblemRecommendationService;
import com.algocoach.service.UserProgressService;
import com.algocoach.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/mvp")
@CrossOrigin(origins = "*")
public class MVPController {
    
    @Autowired
    private ProblemRecommendationService recommendationService;
    
    @Autowired
    private UserProgressService progressService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ProblemRepository problemRepository;
    
    /**
     * Get personalized problem recommendations for the current user
     */
    @GetMapping("/recommendations")
    public ResponseEntity<?> getRecommendations(
            Authentication authentication,
            @RequestParam(defaultValue = "5") int limit) {
        try {
            User user = getCurrentUser(authentication);
            List<Problem> recommendations = recommendationService.getRecommendedProblems(user, limit);
            return ResponseEntity.ok(Map.of("recommendations", recommendations));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get problems by topic
     */
    @GetMapping("/problems/topic/{topic}")
    public ResponseEntity<?> getProblemsByTopic(
            Authentication authentication,
            @PathVariable String topic,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            User user = getCurrentUser(authentication);
            List<Problem> problems = recommendationService.getProblemsByTopic(user, topic, limit);
            return ResponseEntity.ok(Map.of("problems", problems));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get random problems for practice
     */
    @GetMapping("/problems/random")
    public ResponseEntity<?> getRandomProblems(
            Authentication authentication,
            @RequestParam Difficulty difficulty,
            @RequestParam(defaultValue = "3") int limit) {
        try {
            User user = getCurrentUser(authentication);
            List<Problem> problems = recommendationService.getRandomProblems(user, difficulty, limit);
            return ResponseEntity.ok(Map.of("problems", problems));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get personalized problem discovery with advanced filtering
     */
    @GetMapping("/problems/discover")
    public ResponseEntity<?> discoverProblems(
            Authentication authentication,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String topic,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            User user = getCurrentUser(authentication);
            
            // Get user's solved problems to exclude them
            List<UserProgress> solvedProblems = progressService.getSolvedProblems(user);
            Set<Long> solvedProblemIds = solvedProblems.stream()
                    .map(up -> up.getProblem().getId())
                    .collect(Collectors.toSet());
            
            // Get filtered problems
            Difficulty difficultyEnum = null;
            if (difficulty != null) {
                try {
                    difficultyEnum = Difficulty.valueOf(difficulty.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Invalid difficulty level: " + difficulty));
                }
            }
            
            List<Problem> problems = problemRepository.findByFilters(difficultyEnum, topic, null);
            
            // Filter out solved problems
            problems = problems.stream()
                    .filter(p -> !solvedProblemIds.contains(p.getId()))
                    .collect(Collectors.toList());
            
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
            result.put("filters", Map.of(
                "difficulty", difficulty != null ? difficulty : "all",
                "topic", topic != null ? topic : "all",
                "sortBy", sortBy != null ? sortBy : "acceptance"
            ));
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Start working on a problem
     */
    @PostMapping("/problems/{problemId}/start")
    public ResponseEntity<?> startProblem(
            Authentication authentication,
            @PathVariable Long problemId) {
        try {
            User user = getCurrentUser(authentication);
            Problem problem = getProblemById(problemId);
            UserProgress progress = progressService.startProblem(user, problem);
            return ResponseEntity.ok(Map.of("message", "Started working on problem", "progress", progress));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Mark a problem as solved
     */
    @PostMapping("/problems/{problemId}/solve")
    public ResponseEntity<?> solveProblem(
            Authentication authentication,
            @PathVariable Long problemId,
            @RequestParam(required = false) Integer timeSpentMinutes) {
        try {
            User user = getCurrentUser(authentication);
            Problem problem = getProblemById(problemId);
            UserProgress progress = progressService.solveProblem(user, problem, timeSpentMinutes);
            return ResponseEntity.ok(Map.of("message", "Problem solved!", "progress", progress));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Give up on a problem
     */
    @PostMapping("/problems/{problemId}/giveup")
    public ResponseEntity<?> giveUpProblem(
            Authentication authentication,
            @PathVariable Long problemId) {
        try {
            User user = getCurrentUser(authentication);
            Problem problem = getProblemById(problemId);
            UserProgress progress = progressService.giveUpProblem(user, problem);
            return ResponseEntity.ok(Map.of("message", "Marked problem as given up", "progress", progress));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Record an attempt on a problem
     */
    @PostMapping("/problems/{problemId}/attempt")
    public ResponseEntity<?> recordAttempt(
            Authentication authentication,
            @PathVariable Long problemId) {
        try {
            User user = getCurrentUser(authentication);
            Problem problem = getProblemById(problemId);
            UserProgress progress = progressService.recordAttempt(user, problem);
            return ResponseEntity.ok(Map.of("message", "Attempt recorded", "progress", progress));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Use a hint on a problem
     */
    @PostMapping("/problems/{problemId}/hint")
    public ResponseEntity<?> useHint(
            Authentication authentication,
            @PathVariable Long problemId) {
        try {
            User user = getCurrentUser(authentication);
            Problem problem = getProblemById(problemId);
            UserProgress progress = progressService.useHint(user, problem);
            return ResponseEntity.ok(Map.of("message", "Hint used", "progress", progress));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get user's progress statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getUserStats(Authentication authentication) {
        try {
            User user = getCurrentUser(authentication);
            Map<String, Object> stats = recommendationService.getUserProgressStats(user);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get user's progress for all problems
     */
    @GetMapping("/progress")
    public ResponseEntity<?> getUserProgress(Authentication authentication) {
        try {
            User user = getCurrentUser(authentication);
            List<UserProgress> progress = progressService.getUserProgress(user);
            return ResponseEntity.ok(Map.of("progress", progress));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get user's solved problems
     */
    @GetMapping("/progress/solved")
    public ResponseEntity<?> getSolvedProblems(Authentication authentication) {
        try {
            User user = getCurrentUser(authentication);
            List<UserProgress> solved = progressService.getSolvedProblems(user);
            return ResponseEntity.ok(Map.of("solved", solved));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get user's in-progress problems
     */
    @GetMapping("/progress/in-progress")
    public ResponseEntity<?> getInProgressProblems(Authentication authentication) {
        try {
            User user = getCurrentUser(authentication);
            List<UserProgress> inProgress = progressService.getInProgressProblems(user);
            return ResponseEntity.ok(Map.of("inProgress", inProgress));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get user's progress for a specific problem
     */
    @GetMapping("/problems/{problemId}/progress")
    public ResponseEntity<?> getProblemProgress(
            Authentication authentication,
            @PathVariable Long problemId) {
        try {
            User user = getCurrentUser(authentication);
            Problem problem = getProblemById(problemId);
            Optional<UserProgress> progress = progressService.getUserProgress(user, problem);
            return ResponseEntity.ok(Map.of("progress", progress.orElse(null)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get MVP dashboard data
     */
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard(Authentication authentication) {
        try {
            User user = getCurrentUser(authentication);
            
            // Get recommendations
            List<Problem> recommendations = recommendationService.getRecommendedProblems(user, 3);
            
            // Get stats
            Map<String, Object> stats = recommendationService.getUserProgressStats(user);
            
            // Get in-progress problems
            List<UserProgress> inProgress = progressService.getInProgressProblems(user);
            
            // Get recently solved problems
            List<UserProgress> recentlySolved = progressService.getSolvedProblems(user)
                    .stream()
                    .limit(5)
                    .toList();
            
            Map<String, Object> dashboard = new HashMap<>();
            dashboard.put("recommendations", recommendations);
            dashboard.put("stats", stats);
            dashboard.put("inProgress", inProgress);
            dashboard.put("recentlySolved", recentlySolved);
            
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // Helper methods
    private User getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }
    
    private Problem getProblemById(Long problemId) {
        return problemRepository.findById(problemId)
                .orElseThrow(() -> new RuntimeException("Problem not found with id: " + problemId));
    }
}
