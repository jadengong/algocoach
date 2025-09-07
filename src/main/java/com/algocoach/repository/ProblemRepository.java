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
}
