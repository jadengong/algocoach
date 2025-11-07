package com.algocoach.dto;

import com.algocoach.domain.Problem;

public class RecommendationResult {
    private Problem problem;
    private String reason;
    private double score; // Recommendation score (0.0 to 1.0)
    
    public RecommendationResult() {}
    
    public RecommendationResult(Problem problem, String reason, double score) {
        this.problem = problem;
        this.reason = reason;
        this.score = score;
    }
    
    public Problem getProblem() {
        return problem;
    }
    
    public void setProblem(Problem problem) {
        this.problem = problem;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public double getScore() {
        return score;
    }
    
    public void setScore(double score) {
        this.score = score;
    }
}
