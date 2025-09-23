package com.algocoach.repository;

import com.algocoach.domain.ProgressStatus;
import com.algocoach.domain.User;
import com.algocoach.domain.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {
    
    Optional<UserProgress> findByUserAndProblem(User user, com.algocoach.domain.Problem problem);
    
    List<UserProgress> findByUserAndStatus(User user, ProgressStatus status);
    
    List<UserProgress> findByUserOrderByAttemptedAtDesc(User user);
    
    @Query("SELECT up FROM UserProgress up WHERE up.user = :user AND up.status = 'SOLVED' ORDER BY up.solvedAt DESC")
    List<UserProgress> findSolvedProblemsByUser(@Param("user") User user);
    
    @Query("SELECT COUNT(up) FROM UserProgress up WHERE up.user = :user AND up.status = 'SOLVED'")
    long countSolvedProblemsByUser(@Param("user") User user);
    
    @Query("SELECT COUNT(up) FROM UserProgress up WHERE up.user = :user AND up.status = 'IN_PROGRESS'")
    long countInProgressProblemsByUser(@Param("user") User user);
    
    @Query("SELECT up.problem.difficulty, COUNT(up) FROM UserProgress up WHERE up.user = :user AND up.status = 'SOLVED' GROUP BY up.problem.difficulty")
    List<Object[]> countSolvedProblemsByDifficulty(@Param("user") User user);
    
    @Query("SELECT up.problem.topic, COUNT(up) FROM UserProgress up WHERE up.user = :user AND up.status = 'SOLVED' GROUP BY up.problem.topic")
    List<Object[]> countSolvedProblemsByTopic(@Param("user") User user);
    
    boolean existsByUserAndProblemAndStatus(User user, com.algocoach.domain.Problem problem, ProgressStatus status);
    
    @Query("SELECT up FROM UserProgress up WHERE up.user = :user AND up.status = 'NOT_STARTED' ORDER BY up.problem.difficulty, up.problem.title")
    List<UserProgress> findNotStartedProblemsByUser(@Param("user") User user);
}
