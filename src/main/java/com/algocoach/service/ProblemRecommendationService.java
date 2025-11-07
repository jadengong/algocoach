package com.algocoach.service;

import com.algocoach.domain.Difficulty;
import com.algocoach.domain.Problem;
import com.algocoach.domain.ProgressStatus;
import com.algocoach.domain.User;
import com.algocoach.domain.UserProgress;
import com.algocoach.dto.RecommendationResult;
import com.algocoach.repository.ProblemRepository;
import com.algocoach.repository.UserProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class ProblemRecommendationService {
    
    @Autowired
    private ProblemRepository problemRepository;
    
    @Autowired
    private UserProgressRepository userProgressRepository;
    
    // Simple in-memory cache for recommendations (key: userId, value: cached recommendations)
    private final Map<Long, CacheEntry> recommendationCache = new ConcurrentHashMap<>();
    private static final long CACHE_TTL_MINUTES = 10; // Cache for 10 minutes
    
    private static class CacheEntry {
        List<RecommendationResult> recommendations;
        LocalDateTime timestamp;
        
        CacheEntry(List<RecommendationResult> recommendations) {
            this.recommendations = recommendations;
            this.timestamp = LocalDateTime.now();
        }
        
        boolean isExpired() {
            return ChronoUnit.MINUTES.between(timestamp, LocalDateTime.now()) > CACHE_TTL_MINUTES;
        }
    }
    
    /**
     * Get personalized problem recommendations for a user
     */
    public List<Problem> getRecommendedProblems(User user, int limit) {
        // Get user's solved problems
        List<UserProgress> solvedProblems = userProgressRepository.findSolvedProblemsByUser(user);
        Set<Long> solvedProblemIds = solvedProblems.stream()
                .map(up -> up.getProblem().getId())
                .collect(Collectors.toSet());
        
        // Get user's in-progress problems
        List<UserProgress> inProgressProblems = userProgressRepository.findByUserAndStatus(user, com.algocoach.domain.ProgressStatus.IN_PROGRESS);
        Set<Long> inProgressProblemIds = inProgressProblems.stream()
                .map(up -> up.getProblem().getId())
                .collect(Collectors.toSet());
        
        // Analyze user's skill level based on solved problems
        Difficulty recommendedDifficulty = analyzeUserSkillLevel(solvedProblems);
        
        // Get problems by difficulty, excluding solved and in-progress ones
        List<Problem> candidateProblems = problemRepository.findByDifficulty(recommendedDifficulty)
                .stream()
                .filter(p -> !solvedProblemIds.contains(p.getId()) && !inProgressProblemIds.contains(p.getId()))
                .collect(Collectors.toList());
        
        // If not enough problems at recommended difficulty, get from other difficulties
        if (candidateProblems.size() < limit) {
            List<Difficulty> allDifficulties = Arrays.asList(Difficulty.EASY, Difficulty.MEDIUM, Difficulty.HARD);
            for (Difficulty diff : allDifficulties) {
                if (diff != recommendedDifficulty) {
                    List<Problem> additionalProblems = problemRepository.findByDifficulty(diff)
                            .stream()
                            .filter(p -> !solvedProblemIds.contains(p.getId()) && !inProgressProblemIds.contains(p.getId()))
                            .collect(Collectors.toList());
                    candidateProblems.addAll(additionalProblems);
                    if (candidateProblems.size() >= limit) break;
                }
            }
        }
        
        // Sort by acceptance rate (higher acceptance rate = easier to solve)
        candidateProblems.sort((p1, p2) -> Double.compare(p2.getAcceptanceRate(), p1.getAcceptanceRate()));
        
        return candidateProblems.stream().limit(limit).collect(Collectors.toList());
    }
    
    /**
     * Get problems by topic that user hasn't solved yet
     */
    public List<Problem> getProblemsByTopic(User user, String topic, int limit) {
        List<UserProgress> solvedProblems = userProgressRepository.findSolvedProblemsByUser(user);
        Set<Long> solvedProblemIds = solvedProblems.stream()
                .map(up -> up.getProblem().getId())
                .collect(Collectors.toSet());
        
        return problemRepository.findByTopicIgnoreCase(topic)
                .stream()
                .filter(p -> !solvedProblemIds.contains(p.getId()))
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    /**
     * Get random problems for practice
     */
    public List<Problem> getRandomProblems(User user, Difficulty difficulty, int limit) {
        List<UserProgress> solvedProblems = userProgressRepository.findSolvedProblemsByUser(user);
        Set<Long> solvedProblemIds = solvedProblems.stream()
                .map(up -> up.getProblem().getId())
                .collect(Collectors.toSet());
        
        List<Problem> allProblems = problemRepository.findByDifficulty(difficulty);
        List<Problem> availableProblems = allProblems.stream()
                .filter(p -> !solvedProblemIds.contains(p.getId()))
                .collect(Collectors.toList());
        
        Collections.shuffle(availableProblems);
        return availableProblems.stream().limit(limit).collect(Collectors.toList());
    }
    
    /**
     * Analyze user's skill level based on solved problems and confidence scores
     */
    private Difficulty analyzeUserSkillLevel(List<UserProgress> solvedProblems) {
        if (solvedProblems.isEmpty()) {
            return Difficulty.EASY; // Start with easy problems for new users
        }
        
        Map<Difficulty, Long> difficultyCount = solvedProblems.stream()
                .collect(Collectors.groupingBy(
                    up -> up.getProblem().getDifficulty(),
                    Collectors.counting()
                ));
        
        // Calculate average confidence by difficulty
        Map<Difficulty, Double> avgConfidenceByDifficulty = solvedProblems.stream()
                .collect(Collectors.groupingBy(
                    up -> up.getProblem().getDifficulty(),
                    Collectors.averagingDouble(UserProgress::getConfidenceScore)
                ));
        
        long easyCount = difficultyCount.getOrDefault(Difficulty.EASY, 0L);
        long mediumCount = difficultyCount.getOrDefault(Difficulty.MEDIUM, 0L);
        long hardCount = difficultyCount.getOrDefault(Difficulty.HARD, 0L);
        
        double easyConfidence = avgConfidenceByDifficulty.getOrDefault(Difficulty.EASY, 0.0);
        double mediumConfidence = avgConfidenceByDifficulty.getOrDefault(Difficulty.MEDIUM, 0.0);
        double hardConfidence = avgConfidenceByDifficulty.getOrDefault(Difficulty.HARD, 0.0);
        
        // Enhanced skill level analysis with confidence scoring
        if (easyCount < 3 || easyConfidence < 0.6) {
            return Difficulty.EASY;
        } else if (mediumCount < 2 || mediumConfidence < 0.5) {
            return Difficulty.MEDIUM;
        } else if (hardCount < 1 || hardConfidence < 0.4) {
            return Difficulty.HARD;
        } else {
            // Advanced user - recommend based on confidence levels
            if (mediumConfidence > 0.7) {
                return Difficulty.HARD;
            } else if (easyConfidence > 0.8) {
                return Difficulty.MEDIUM;
            } else {
                return Difficulty.MEDIUM; // Balanced practice
            }
        }
    }
    
    /**
     * Get user's progress statistics
     */
    public Map<String, Object> getUserProgressStats(User user) {
        long totalSolved = userProgressRepository.countSolvedProblemsByUser(user);
        long inProgress = userProgressRepository.countInProgressProblemsByUser(user);
        long totalProblems = problemRepository.getTotalProblemCount();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSolved", totalSolved);
        stats.put("inProgress", inProgress);
        stats.put("totalProblems", totalProblems);
        stats.put("completionRate", totalProblems > 0 ? (double) totalSolved / totalProblems * 100 : 0.0);
        
        // Solved by difficulty
        List<Object[]> solvedByDifficulty = userProgressRepository.countSolvedProblemsByDifficulty(user);
        Map<String, Long> difficultyStats = new HashMap<>();
        for (Object[] result : solvedByDifficulty) {
            difficultyStats.put(result[0].toString(), (Long) result[1]);
        }
        stats.put("solvedByDifficulty", difficultyStats);
        
        // Solved by topic
        List<Object[]> solvedByTopic = userProgressRepository.countSolvedProblemsByTopic(user);
        Map<String, Long> topicStats = new HashMap<>();
        for (Object[] result : solvedByTopic) {
            topicStats.put(result[0].toString(), (Long) result[1]);
        }
        stats.put("solvedByTopic", topicStats);
        
        // Add confidence statistics
        List<UserProgress> solvedProblems = userProgressRepository.findSolvedProblemsByUser(user);
        double avgConfidence = solvedProblems.stream()
                .mapToDouble(UserProgress::getConfidenceScore)
                .average()
                .orElse(0.0);
        stats.put("averageConfidence", Math.round(avgConfidence * 100.0) / 100.0);
        
        // Confidence by difficulty
        Map<String, Double> confidenceByDifficulty = new HashMap<>();
        for (Object[] result : solvedByDifficulty) {
            String difficulty = result[0].toString();
            List<UserProgress> problemsOfDifficulty = solvedProblems.stream()
                    .filter(up -> up.getProblem().getDifficulty().toString().equals(difficulty))
                    .toList();
            if (!problemsOfDifficulty.isEmpty()) {
                double avgConf = problemsOfDifficulty.stream()
                        .mapToDouble(UserProgress::getConfidenceScore)
                        .average()
                        .orElse(0.0);
                confidenceByDifficulty.put(difficulty, Math.round(avgConf * 100.0) / 100.0);
            }
        }
        stats.put("confidenceByDifficulty", confidenceByDifficulty);
        
        return stats;
    }
    
    /**
     * Get enhanced personalized recommendations with explanations
     * Includes: topic-based recommendations, spaced repetition, smooth difficulty progression
     */
    public List<RecommendationResult> getEnhancedRecommendations(User user, int limit) {
        // Check cache first
        CacheEntry cached = recommendationCache.get(user.getId());
        if (cached != null && !cached.isExpired()) {
            return cached.recommendations.stream().limit(limit).collect(Collectors.toList());
        }
        
        List<UserProgress> solvedProblems = userProgressRepository.findSolvedProblemsByUser(user);
        Set<Long> solvedProblemIds = solvedProblems.stream()
                .map(up -> up.getProblem().getId())
                .collect(Collectors.toSet());
        
        List<UserProgress> inProgressProblems = userProgressRepository.findByUserAndStatus(user, ProgressStatus.IN_PROGRESS);
        Set<Long> inProgressProblemIds = inProgressProblems.stream()
                .map(up -> up.getProblem().getId())
                .collect(Collectors.toSet());
        
        // Get all available problems
        List<Problem> allProblems = problemRepository.findAll();
        
        // Build recommendation candidates with scores
        List<RecommendationResult> candidates = new ArrayList<>();
        
        for (Problem problem : allProblems) {
            // Skip in-progress problems, but include solved ones for spaced repetition
            if (inProgressProblemIds.contains(problem.getId())) {
                continue;
            }
            
            boolean isSolved = solvedProblemIds.contains(problem.getId());
            double score = 0.0;
            String reason = "";
            
            // 2. Spaced repetition (revisit solved problems after time) - check first
            double spacedRepetitionScore = calculateSpacedRepetitionScore(user, problem, solvedProblems);
            if (spacedRepetitionScore > 0) {
                score += spacedRepetitionScore * 0.5; // 50% weight for spaced repetition
                reason += "Time to review this problem. ";
            } else if (!isSolved) {
                // Only apply other scores to unsolved problems
                // 1. Topic-based recommendation (weak areas)
                double topicScore = calculateTopicScore(user, problem, solvedProblems);
                if (topicScore > 0.3) {
                    score += topicScore * 0.4; // 40% weight for weak topics
                    reason += "Focus area: " + problem.getTopic() + ". ";
                }
                
                // 3. Difficulty progression (smooth transitions)
                double difficultyScore = calculateDifficultyScore(user, problem, solvedProblems);
                score += difficultyScore * 0.3; // 30% weight for appropriate difficulty
                if (difficultyScore > 0.5) {
                    reason += "Matches your current skill level. ";
                }
            }
            
            if (score > 0) {
                candidates.add(new RecommendationResult(problem, reason.trim(), score));
            }
        }
        
        // Sort by score (highest first)
        candidates.sort((r1, r2) -> Double.compare(r2.getScore(), r1.getScore()));
        
        // Take top recommendations
        List<RecommendationResult> recommendations = candidates.stream()
                .limit(limit)
                .collect(Collectors.toList());
        
        // Cache the results
        recommendationCache.put(user.getId(), new CacheEntry(recommendations));
        
        return recommendations;
    }
    
    /**
     * Calculate score based on weak topics (topics with low confidence or few solved problems)
     */
    private double calculateTopicScore(User user, Problem problem, List<UserProgress> solvedProblems) {
        Map<String, Long> topicCount = solvedProblems.stream()
                .collect(Collectors.groupingBy(
                    up -> up.getProblem().getTopic(),
                    Collectors.counting()
                ));
        
        Map<String, Double> topicConfidence = solvedProblems.stream()
                .collect(Collectors.groupingBy(
                    up -> up.getProblem().getTopic(),
                    Collectors.averagingDouble(UserProgress::getConfidenceScore)
                ));
        
        String problemTopic = problem.getTopic();
        long solvedInTopic = topicCount.getOrDefault(problemTopic, 0L);
        double avgConfidenceInTopic = topicConfidence.getOrDefault(problemTopic, 0.0);
        
        // Higher score if user has solved few problems in this topic or has low confidence
        if (solvedInTopic == 0) {
            return 1.0; // New topic - highest priority
        } else if (solvedInTopic < 3) {
            return 0.8; // Few problems solved in this topic
        } else if (avgConfidenceInTopic < 0.6) {
            return 0.6; // Low confidence in this topic
        }
        
        return 0.0;
    }
    
    /**
     * Calculate score for spaced repetition (problems solved long ago should be revisited)
     */
    private double calculateSpacedRepetitionScore(User user, Problem problem, List<UserProgress> solvedProblems) {
        Optional<UserProgress> previousProgress = solvedProblems.stream()
                .filter(up -> up.getProblem().getId().equals(problem.getId()))
                .findFirst();
        
        if (previousProgress.isPresent() && previousProgress.get().getSolvedAt() != null) {
            LocalDateTime solvedAt = previousProgress.get().getSolvedAt();
            long daysSinceSolved = ChronoUnit.DAYS.between(solvedAt, LocalDateTime.now());
            
            // Recommend revisiting after 7 days, with increasing priority up to 30 days
            if (daysSinceSolved >= 7 && daysSinceSolved <= 30) {
                return Math.min(1.0, (daysSinceSolved - 7) / 23.0); // Scale from 0 to 1
            } else if (daysSinceSolved > 30) {
                return 0.5; // Still recommend but lower priority
            }
        }
        
        return 0.0;
    }
    
    /**
     * Calculate score based on smooth difficulty progression
     */
    private Difficulty calculateRecommendedDifficulty(List<UserProgress> solvedProblems) {
        if (solvedProblems.isEmpty()) {
            return Difficulty.EASY;
        }
        
        Map<Difficulty, Long> difficultyCount = solvedProblems.stream()
                .collect(Collectors.groupingBy(
                    up -> up.getProblem().getDifficulty(),
                    Collectors.counting()
                ));
        
        Map<Difficulty, Double> avgConfidence = solvedProblems.stream()
                .collect(Collectors.groupingBy(
                    up -> up.getProblem().getDifficulty(),
                    Collectors.averagingDouble(UserProgress::getConfidenceScore)
                ));
        
        long easyCount = difficultyCount.getOrDefault(Difficulty.EASY, 0L);
        long mediumCount = difficultyCount.getOrDefault(Difficulty.MEDIUM, 0L);
        long hardCount = difficultyCount.getOrDefault(Difficulty.HARD, 0L);
        
        double easyConf = avgConfidence.getOrDefault(Difficulty.EASY, 0.0);
        double mediumConf = avgConfidence.getOrDefault(Difficulty.MEDIUM, 0.0);
        double hardConf = avgConfidence.getOrDefault(Difficulty.HARD, 0.0);
        
        // Smooth progression logic
        if (easyCount < 5 || easyConf < 0.7) {
            return Difficulty.EASY;
        } else if (mediumCount < 3 || mediumConf < 0.65) {
            return Difficulty.MEDIUM;
        } else if (hardCount < 2 || hardConf < 0.6) {
            return Difficulty.HARD;
        } else {
            // Advanced: mix of medium and hard
            if (mediumConf > 0.75) {
                return Difficulty.HARD;
            }
            return Difficulty.MEDIUM;
        }
    }
    
    /**
     * Calculate score based on how well the problem matches recommended difficulty
     */
    private double calculateDifficultyScore(User user, Problem problem, List<UserProgress> solvedProblems) {
        Difficulty recommended = calculateRecommendedDifficulty(solvedProblems);
        Difficulty problemDifficulty = problem.getDifficulty();
        
        if (problemDifficulty == recommended) {
            return 1.0; // Perfect match
        } else if (Math.abs(problemDifficulty.ordinal() - recommended.ordinal()) == 1) {
            return 0.6; // One level away - still good
        } else {
            return 0.2; // Far from recommended - lower priority
        }
    }
    
    /**
     * Clear cache for a user (call when user solves a problem)
     */
    public void clearUserCache(Long userId) {
        recommendationCache.remove(userId);
    }
}
