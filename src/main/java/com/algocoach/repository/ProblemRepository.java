package com.algocoach.repository;

import com.algocoach.domain.Problem;
import com.algocoach.domain.Difficulty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, Long> {
    
    List<Problem> findByDifficulty(Difficulty difficulty);
    
    List<Problem> findByTopic(String topic);
    
    List<Problem> findByDifficultyAndTopic(Difficulty difficulty, String topic);
}
