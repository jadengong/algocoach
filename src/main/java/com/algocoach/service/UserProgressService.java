package com.algocoach.service;

import com.algocoach.domain.Problem;
import com.algocoach.domain.ProgressStatus;
import com.algocoach.domain.User;
import com.algocoach.domain.UserProgress;
import com.algocoach.repository.UserProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserProgressService {
    
    @Autowired
    private UserProgressRepository userProgressRepository;
    
    /**
     * Start working on a problem
     */
    public UserProgress startProblem(User user, Problem problem) {
        Optional<UserProgress> existingProgress = userProgressRepository.findByUserAndProblem(user, problem);
        
        if (existingProgress.isPresent()) {
            UserProgress progress = existingProgress.get();
            if (progress.getStatus() == ProgressStatus.NOT_STARTED) {
                progress.setStatus(ProgressStatus.IN_PROGRESS);
                progress.setAttemptedAt(LocalDateTime.now());
                progress.setAttemptsCount(progress.getAttemptsCount() + 1);
                return userProgressRepository.save(progress);
            }
            return progress;
        } else {
            UserProgress newProgress = new UserProgress(user, problem, ProgressStatus.IN_PROGRESS);
            return userProgressRepository.save(newProgress);
        }
    }
    
    /**
     * Mark a problem as solved
     */
    public UserProgress solveProblem(User user, Problem problem, Integer timeSpentMinutes) {
        Optional<UserProgress> existingProgress = userProgressRepository.findByUserAndProblem(user, problem);
        
        UserProgress progress;
        if (existingProgress.isPresent()) {
            progress = existingProgress.get();
            progress.setStatus(ProgressStatus.SOLVED);
            progress.setSolvedAt(LocalDateTime.now());
            if (timeSpentMinutes != null) {
                progress.setTimeSpentMinutes(timeSpentMinutes);
            }
        } else {
            progress = new UserProgress(user, problem, ProgressStatus.SOLVED);
            progress.setSolvedAt(LocalDateTime.now());
            if (timeSpentMinutes != null) {
                progress.setTimeSpentMinutes(timeSpentMinutes);
            }
        }
        
        // Calculate confidence score based on solving performance
        double confidenceScore = calculateConfidenceScore(progress);
        progress.setConfidenceScore(confidenceScore);
        
        return userProgressRepository.save(progress);
    }
    
    /**
     * Mark a problem as given up
     */
    public UserProgress giveUpProblem(User user, Problem problem) {
        Optional<UserProgress> existingProgress = userProgressRepository.findByUserAndProblem(user, problem);
        
        UserProgress progress;
        if (existingProgress.isPresent()) {
            progress = existingProgress.get();
            progress.setStatus(ProgressStatus.GAVE_UP);
        } else {
            progress = new UserProgress(user, problem, ProgressStatus.GAVE_UP);
        }
        
        return userProgressRepository.save(progress);
    }
    
    /**
     * Get user's progress for a specific problem
     */
    public Optional<UserProgress> getUserProgress(User user, Problem problem) {
        return userProgressRepository.findByUserAndProblem(user, problem);
    }
    
    /**
     * Get all user's progress
     */
    public List<UserProgress> getUserProgress(User user) {
        return userProgressRepository.findByUserOrderByAttemptedAtDesc(user);
    }
    
    /**
     * Get user's solved problems
     */
    public List<UserProgress> getSolvedProblems(User user) {
        return userProgressRepository.findSolvedProblemsByUser(user);
    }
    
    /**
     * Get user's in-progress problems
     */
    public List<UserProgress> getInProgressProblems(User user) {
        return userProgressRepository.findByUserAndStatus(user, ProgressStatus.IN_PROGRESS);
    }
    
    /**
     * Update attempt count
     */
    public UserProgress recordAttempt(User user, Problem problem) {
        Optional<UserProgress> existingProgress = userProgressRepository.findByUserAndProblem(user, problem);
        
        UserProgress progress;
        if (existingProgress.isPresent()) {
            progress = existingProgress.get();
            progress.setAttemptsCount(progress.getAttemptsCount() + 1);
        } else {
            progress = new UserProgress(user, problem, ProgressStatus.IN_PROGRESS);
        }
        
        return userProgressRepository.save(progress);
    }
    
    /**
     * Use a hint
     */
    public UserProgress useHint(User user, Problem problem) {
        Optional<UserProgress> existingProgress = userProgressRepository.findByUserAndProblem(user, problem);
        
        UserProgress progress;
        if (existingProgress.isPresent()) {
            progress = existingProgress.get();
            progress.setHintsUsed(progress.getHintsUsed() + 1);
        } else {
            progress = new UserProgress(user, problem, ProgressStatus.IN_PROGRESS);
            progress.setHintsUsed(1);
        }
        
        return userProgressRepository.save(progress);
    }
    
    /**
     * Mark a problem as solved with explicit confidence score
     */
    public UserProgress solveProblem(User user, Problem problem, Integer timeSpentMinutes, Double confidenceScore) {
        Optional<UserProgress> existingProgress = userProgressRepository.findByUserAndProblem(user, problem);
        
        UserProgress progress;
        if (existingProgress.isPresent()) {
            progress = existingProgress.get();
            progress.setStatus(ProgressStatus.SOLVED);
            progress.setSolvedAt(LocalDateTime.now());
            if (timeSpentMinutes != null) {
                progress.setTimeSpentMinutes(timeSpentMinutes);
            }
        } else {
            progress = new UserProgress(user, problem, ProgressStatus.SOLVED);
            progress.setSolvedAt(LocalDateTime.now());
            if (timeSpentMinutes != null) {
                progress.setTimeSpentMinutes(timeSpentMinutes);
            }
        }
        
        // Use provided confidence score or calculate one
        if (confidenceScore != null) {
            progress.setConfidenceScore(Math.max(0.0, Math.min(1.0, confidenceScore))); // Clamp between 0 and 1
        } else {
            double calculatedScore = calculateConfidenceScore(progress);
            progress.setConfidenceScore(calculatedScore);
        }
        
        return userProgressRepository.save(progress);
    }
    
    /**
     * Calculate confidence score based on solving performance
     * Higher score = more confident (fewer attempts, less time, fewer hints)
     */
    private double calculateConfidenceScore(UserProgress progress) {
        double score = 1.0; // Start with perfect confidence
        
        // Penalize for multiple attempts (each attempt reduces confidence by 0.15)
        score -= (progress.getAttemptsCount() - 1) * 0.15;
        
        // Penalize for using hints (each hint reduces confidence by 0.2)
        score -= progress.getHintsUsed() * 0.2;
        
        // Adjust based on time spent (if available)
        if (progress.getTimeSpentMinutes() != null) {
            int timeSpent = progress.getTimeSpentMinutes();
            // If took more than 45 minutes, reduce confidence
            if (timeSpent > 45) {
                score -= 0.1;
            }
            // If took more than 90 minutes, reduce more
            if (timeSpent > 90) {
                score -= 0.2;
            }
        }
        
        // Ensure score is between 0 and 1
        return Math.max(0.0, Math.min(1.0, score));
    }
}
