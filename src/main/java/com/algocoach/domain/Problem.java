package com.algocoach.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "problems")
public class Problem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty;
    
    @Column(nullable = false)
    private String topic;
    
    @Column(name = "acceptance_rate")
    private Double acceptanceRate;
    
    @Column(name = "leetcode_id")
    private Long leetcodeId;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(columnDefinition = "TEXT")
    private String examples;
    
    @Column(columnDefinition = "TEXT")
    private String constraints;
    
    // Default constructor
    public Problem() {}
    
    // Constructor with required fields
    public Problem(String title, Difficulty difficulty, String topic, Double acceptanceRate, Long leetcodeId) {
        this.title = title;
        this.difficulty = difficulty;
        this.topic = topic;
        this.acceptanceRate = acceptanceRate;
        this.leetcodeId = leetcodeId;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public Difficulty getDifficulty() {
        return difficulty;
    }
    
    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }
    
    public String getTopic() {
        return topic;
    }
    
    public void setTopic(String topic) {
        this.topic = topic;
    }
    
    public Double getAcceptanceRate() {
        return acceptanceRate;
    }
    
    public void setAcceptanceRate(Double acceptanceRate) {
        this.acceptanceRate = acceptanceRate;
    }
    
    public Long getLeetcodeId() {
        return leetcodeId;
    }
    
    public void setLeetcodeId(Long leetcodeId) {
        this.leetcodeId = leetcodeId;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getExamples() {
        return examples;
    }
    
    public void setExamples(String examples) {
        this.examples = examples;
    }
    
    public String getConstraints() {
        return constraints;
    }
    
    public void setConstraints(String constraints) {
        this.constraints = constraints;
    }
    
    @Override
    public String toString() {
        return "Problem{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", difficulty=" + difficulty +
                ", topic='" + topic + '\'' +
                ", acceptanceRate=" + acceptanceRate +
                ", leetcodeId=" + leetcodeId +
                '}';
    }
}
