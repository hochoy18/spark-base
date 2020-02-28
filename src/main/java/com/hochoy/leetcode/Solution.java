package com.hochoy.leetcode;

import org.apache.spark.sql.sources.In;

public class Solution {


    public static void main(String[] args) {

        boolean i = isPalindrome(0);
        System.out.println(i);
        int i1 = reverseInteger(123);
        System.out.println(i1);
    }


    /**
     * Given an array of integers, return indices of the two numbers such that they add up to a specific target.
     * You may assume that each input would have exactly one solution, and you may not use the same element twice.
     *
     * @param nums
     * @param target
     * @return
     */
    public static int[] twoSum(int[] nums, int target) {
        if (nums == null || nums.length < 2) {
            return new int[]{};
        }
        for (int i = 0; i < nums.length - 1; i++) {
            for (int j = 1; j < nums.length; j++) {
                if (nums[i] + nums[j] == target && i != j) {
                    return new int[]{i, j};
                }
            }
        }
        return new int[]{};

    }


    public static int reverseInteger(int x) {
        System.out.println(x);
        int y =0;
        for (;x!=0 ;x/=10){
            if (y > Integer.MAX_VALUE / 10 || y < Integer.MIN_VALUE) {
                return 0;
            }
            y = 10 * y + x % 10;
        }
        while (x!= 0){

            if (y > Integer.MAX_VALUE /10 || y < Integer.MIN_VALUE){
                return 0;
            }
            y = y * 10 + x % 10;
            x /= 10;

        }
        return y;
    }

    public static boolean isPalindrome(int x) {
        boolean flag = false;
        if (x>= 0){
            if (reverseInteger(x) == x){
                flag = true;
            }
        }
        return flag;
    }



    public static int[] getQuotientsAndRemainders(int dividend, int divisor) {

        if (dividend == 0 || dividend == 0) {
            return new int[]{0, 0};
        }
        return new int[]{dividend / divisor, dividend % divisor};
    }
}
