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
        Problem twoSum = new Problem("Two Sum", Difficulty.EASY, "Array", 45.8, 1L);
        twoSum.setDescription("Given an array of integers nums and an integer target, return indices of the two numbers such that they add up to target.");
        twoSum.setExamples("Input: nums = [2,7,11,15], target = 9\nOutput: [0,1]\nExplanation: Because nums[0] + nums[1] == 9, we return [0, 1].");
        twoSum.setConstraints("2 <= nums.length <= 10^4\n-10^9 <= nums[i] <= 10^9\n-10^9 <= target <= 10^9");
        problemRepository.save(twoSum);
        
        Problem validParentheses = new Problem("Valid Parentheses", Difficulty.EASY, "Stack", 38.4, 2L);
        validParentheses.setDescription("Given a string s containing just the characters '(', ')', '{', '}', '[' and ']', determine if the input string is valid.");
        validParentheses.setExamples("Input: s = \"()\"\nOutput: true\n\nInput: s = \"()[]{}\"\nOutput: true\n\nInput: s = \"(]\"\nOutput: false");
        validParentheses.setConstraints("1 <= s.length <= 10^4\ns consists of parentheses only '()[]{}'.");
        problemRepository.save(validParentheses);
        
        Problem maxSubarray = new Problem("Maximum Subarray", Difficulty.EASY, "Array", 50.2, 3L);
        maxSubarray.setDescription("Given an integer array nums, find the contiguous subarray (containing at least one number) which has the largest sum and return its sum.");
        maxSubarray.setExamples("Input: nums = [-2,1,-3,4,-1,2,1,-5,4]\nOutput: 6\nExplanation: [4,-1,2,1] has the largest sum = 6.");
        maxSubarray.setConstraints("1 <= nums.length <= 10^5\n-10^4 <= nums[i] <= 10^4");
        problemRepository.save(maxSubarray);
        
        Problem climbingStairs = new Problem("Climbing Stairs", Difficulty.EASY, "Dynamic Programming", 51.3, 4L);
        climbingStairs.setDescription("You are climbing a staircase. It takes n steps to reach the top. Each time you can either climb 1 or 2 steps. In how many distinct ways can you climb to the top?");
        climbingStairs.setExamples("Input: n = 2\nOutput: 2\nExplanation: There are two ways to climb to the top.\n1. 1 step + 1 step\n2. 2 steps");
        climbingStairs.setConstraints("1 <= n <= 45");
        problemRepository.save(climbingStairs);
        
        Problem bestTimeToBuy = new Problem("Best Time to Buy and Sell Stock", Difficulty.EASY, "Array", 49.7, 5L);
        bestTimeToBuy.setDescription("You are given an array prices where prices[i] is the price of a given stock on the ith day. You want to maximize your profit by choosing a single day to buy one stock and choosing a different day in the future to sell that stock.");
        bestTimeToBuy.setExamples("Input: prices = [7,1,5,3,6,4]\nOutput: 5\nExplanation: Buy on day 2 (price = 1) and sell on day 5 (price = 6), profit = 6-1 = 5.");
        bestTimeToBuy.setConstraints("1 <= prices.length <= 10^5\n0 <= prices[i] <= 10^4");
        problemRepository.save(bestTimeToBuy);
        
        // Medium Problems
        Problem addTwoNumbers = new Problem("Add Two Numbers", Difficulty.MEDIUM, "Linked List", 36.8, 6L);
        addTwoNumbers.setDescription("You are given two non-empty linked lists representing two non-negative integers. The digits are stored in reverse order, and each of their nodes contains a single digit. Add the two numbers and return the sum as a linked list.");
        addTwoNumbers.setExamples("Input: l1 = [2,4,3], l2 = [5,6,4]\nOutput: [7,0,8]\nExplanation: 342 + 465 = 807.");
        addTwoNumbers.setConstraints("The number of nodes in each linked list is in the range [1, 100].\n0 <= Node.val <= 9\nIt is guaranteed that the list represents a number that does not have leading zeros.");
        problemRepository.save(addTwoNumbers);
        
        Problem longestSubstring = new Problem("Longest Substring Without Repeating Characters", Difficulty.MEDIUM, "Hash Table", 33.2, 7L);
        longestSubstring.setDescription("Given a string s, find the length of the longest substring without repeating characters.");
        longestSubstring.setExamples("Input: s = \"abcabcbb\"\nOutput: 3\nExplanation: The answer is \"abc\", with the length of 3.");
        longestSubstring.setConstraints("0 <= s.length <= 5 * 10^4\ns consists of English letters, digits, symbols and spaces.");
        problemRepository.save(longestSubstring);
        
        // Hard Problems
        Problem medianOfTwoArrays = new Problem("Median of Two Sorted Arrays", Difficulty.HARD, "Array", 35.2, 11L);
        medianOfTwoArrays.setDescription("Given two sorted arrays nums1 and nums2 of size m and n respectively, return the median of the two sorted arrays.");
        medianOfTwoArrays.setExamples("Input: nums1 = [1,3], nums2 = [2]\nOutput: 2.00000\nExplanation: merged array = [1,2,3] and median is 2.");
        medianOfTwoArrays.setConstraints("nums1.length == m\nnums2.length == n\n0 <= m <= 1000\n0 <= n <= 1000\n1 <= m + n <= 2000");
        problemRepository.save(medianOfTwoArrays);
        
        System.out.println("Initialized " + problemRepository.count() + " problems in the database.");
    }
}
