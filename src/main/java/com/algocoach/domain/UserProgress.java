package com.algocoach.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_progress")
public class UserProgress {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProgressStatus status;
    
    @Column(name = "attempted_at")
    private LocalDateTime attemptedAt;
    
    @Column(name = "solved_at")
    private LocalDateTime solvedAt;
    
    @Column(name = "time_spent_minutes")
    private Integer timeSpentMinutes;
    
    @Column(name = "attempts_count")
    private Integer attemptsCount = 0;
    
    @Column(name = "hints_used")
    private Integer hintsUsed = 0;
    
    @Column(name = "confidence_score")
    private Double confidenceScore = 0.0; // 0.0 to 1.0, represents how confident user was solving this problem
    
    @Column(name = "is_bookmarked")
    private Boolean isBookmarked = false; // Whether user has bookmarked this problem
    
    // Default constructor
    public UserProgress() {}
    
    // Constructor with required fields
    public UserProgress(User user, Problem problem, ProgressStatus status) {
        this.user = user;
        this.problem = problem;
        this.status = status;
        this.attemptedAt = LocalDateTime.now();
        this.attemptsCount = 1;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Problem getProblem() {
        return problem;
    }
    
    public void setProblem(Problem problem) {
        this.problem = problem;
    }
    
    public ProgressStatus getStatus() {
        return status;
    }
    
    public void setStatus(ProgressStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getAttemptedAt() {
        return attemptedAt;
    }
    
    public void setAttemptedAt(LocalDateTime attemptedAt) {
        this.attemptedAt = attemptedAt;
    }
    
    public LocalDateTime getSolvedAt() {
        return solvedAt;
    }
    
    public void setSolvedAt(LocalDateTime solvedAt) {
        this.solvedAt = solvedAt;
    }
    
    public Integer getTimeSpentMinutes() {
        return timeSpentMinutes;
    }
    
    public void setTimeSpentMinutes(Integer timeSpentMinutes) {
        this.timeSpentMinutes = timeSpentMinutes;
    }
    
    public Integer getAttemptsCount() {
        return attemptsCount;
    }
    
    public void setAttemptsCount(Integer attemptsCount) {
        this.attemptsCount = attemptsCount;
    }
    
    public Integer getHintsUsed() {
        return hintsUsed;
    }
    
    public void setHintsUsed(Integer hintsUsed) {
        this.hintsUsed = hintsUsed;
    }
    
    public Double getConfidenceScore() {
        return confidenceScore;
    }
    
    public void setConfidenceScore(Double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }
    
    public Boolean getIsBookmarked() {
        return isBookmarked;
    }
    
    public void setIsBookmarked(Boolean isBookmarked) {
        this.isBookmarked = isBookmarked;
    }
    
    @Override
    public String toString() {
        return "UserProgress{" +
                "id=" + id +
                ", user=" + (user != null ? user.getUsername() : "null") +
                ", problem=" + (problem != null ? problem.getTitle() : "null") +
                ", status=" + status +
                ", attemptedAt=" + attemptedAt +
                ", solvedAt=" + solvedAt +
                ", attemptsCount=" + attemptsCount +
                '}';
    }
}
