package com.algocoach.config;

import com.algocoach.domain.Difficulty;
import com.algocoach.domain.Problem;
import com.algocoach.repository.ProblemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private ProblemRepository problemRepository;
    
    @Override
    public void run(String... args) throws Exception {
        // Only initialize if database is empty
        if (problemRepository.count() == 0) {
            initializeProblems();
        }
    }
    
    private void initializeProblems() {
        // Easy Problems
        problemRepository.save(new Problem("Two Sum", Difficulty.EASY, "Array", 45.8, 1L));
        problemRepository.save(new Problem("Valid Parentheses", Difficulty.EASY, "Stack", 38.4, 2L));
        problemRepository.save(new Problem("Maximum Subarray", Difficulty.EASY, "Array", 50.2, 3L));
        problemRepository.save(new Problem("Climbing Stairs", Difficulty.EASY, "Dynamic Programming", 51.3, 4L));
        problemRepository.save(new Problem("Best Time to Buy and Sell Stock", Difficulty.EASY, "Array", 49.7, 5L));
        
        // Medium Problems
        problemRepository.save(new Problem("Add Two Numbers", Difficulty.MEDIUM, "Linked List", 36.8, 6L));
        problemRepository.save(new Problem("Longest Substring Without Repeating Characters", Difficulty.MEDIUM, "Hash Table", 33.2, 7L));
        problemRepository.save(new Problem("Longest Palindromic Substring", Difficulty.MEDIUM, "String", 31.5, 8L));
        problemRepository.save(new Problem("Zigzag Conversion", Difficulty.MEDIUM, "String", 40.1, 9L));
        problemRepository.save(new Problem("Reverse Integer", Difficulty.MEDIUM, "Math", 26.8, 10L));
        
        // Hard Problems
        problemRepository.save(new Problem("Median of Two Sorted Arrays", Difficulty.HARD, "Array", 35.2, 11L));
        problemRepository.save(new Problem("Regular Expression Matching", Difficulty.HARD, "String", 28.9, 12L));
        problemRepository.save(new Problem("Merge k Sorted Lists", Difficulty.HARD, "Linked List", 44.7, 13L));
        problemRepository.save(new Problem("First Missing Positive", Difficulty.HARD, "Array", 33.1, 14L));
        problemRepository.save(new Problem("Trapping Rain Water", Difficulty.HARD, "Array", 52.3, 15L));
        
        System.out.println("Initialized " + problemRepository.count() + " problems in the database.");
    }
}
