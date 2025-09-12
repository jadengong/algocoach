package com.algocoach.repository;

import com.algocoach.domain.Difficulty;
import com.algocoach.domain.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, Long> {
    
    List<Problem> findByDifficulty(Difficulty difficulty);
    
    List<Problem> findByTopicIgnoreCase(String topic);
    
    List<Problem> findByTitleContainingIgnoreCase(String title);
    
    @Query("SELECT p FROM Problem p WHERE " +
           "(:difficulty IS NULL OR p.difficulty = :difficulty) AND " +
           "(:topic IS NULL OR LOWER(p.topic) = LOWER(:topic)) AND " +
           "(:title IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :title, '%')))")
    List<Problem> findByFilters(@Param("difficulty") Difficulty difficulty,
                               @Param("topic") String topic,
                               @Param("title") String title);
    
    List<Problem> findByLeetcodeId(Long leetcodeId);
    
    long countByDifficulty(Difficulty difficulty);
    
    long countByTopicIgnoreCase(String topic);
    
    // Find problems by multiple topics
    List<Problem> findByTopicInIgnoreCase(List<String> topics);
    
    // Find problems by difficulty and topic combination
    List<Problem> findByDifficultyAndTopicIgnoreCase(Difficulty difficulty, String topic);
    
    // Get random problems by difficulty
    @Query(value = "SELECT * FROM problem WHERE difficulty = :difficulty ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<Problem> findRandomByDifficulty(@Param("difficulty") String difficulty, @Param("limit") int limit);
    
    // Find problems with specific difficulty levels
    List<Problem> findByDifficultyIn(List<Difficulty> difficulties);
    
    // Count total problems
    @Query("SELECT COUNT(p) FROM Problem p")
    long getTotalProblemCount();
    
    // Find problems by partial title match (case insensitive)
    @Query("SELECT p FROM Problem p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Problem> findByTitleKeyword(@Param("keyword") String keyword);
}
