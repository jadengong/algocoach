package com.algocoach.service;

import com.algocoach.domain.Difficulty;
import com.algocoach.domain.Problem;
import com.algocoach.domain.User;
import com.algocoach.domain.UserProgress;
import com.algocoach.repository.ProblemRepository;
import com.algocoach.repository.UserProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProblemRecommendationService {
    
    @Autowired
    private ProblemRepository problemRepository;
    
    @Autowired
    private UserProgressRepository userProgressRepository;
    
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
}
