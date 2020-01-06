package com.hochoy.Algorithms_dataStructures.array;

import java.util.ArrayList;
import java.util.List;

public class ArraySolution {

   static ArraySolution solution = new ArraySolution();

    public static void main(String[] args) {


        int[] nums = {-1, 0, 1, 2, -1, -4};
        solution.threeSum(nums);

      /* int [] nums = {2, 7, 11, 7};
       int target = 14;
        int[] ints = solution.twoSum(nums, target);
        System.out.println(ints);*/
    }

    /**
     *
     *
     * @see <a href="https://leetcode.com/problems/two-sum/">Two Sum</a>
     * @param nums
     * @param target
     * @return
     */
    public int[] twoSum(int[] nums, int target) {
        int [] res = new int[]{};
        if (nums.length<2){
            res = new int[]{};
        }
        for (int i = 0; i < nums.length -1; i++) {
            for (int j = 1; j < nums.length; j++) {
                if (nums[i] + nums[j] == target && i != j ) {
                    res = new int []{i,j};
                    return  res;
                }
            }
        }
        return res;
    }


    /**
     * @see <a href='https://leetcode.com/problems/3sum/'>3 sum</a>
     * @param nums
     * @return
     */
    public List<List<Integer>> threeSum(int[] nums) {
        if (nums.length<3){
            return new ArrayList<>();
        }
        List<List<Integer>>  res = new ArrayList<>();
        for (int i = 0; i < nums.length; i++) {
            for (int j = 1; j < nums.length -1 ; j++) {
                for (int k = 2; k < nums.length -2; k++) {
                    if (nums[i] + nums[j] + nums[k] == 0 && i!=j && j != k && i!= k){
                        List<Integer> list = new ArrayList<>();
                        list.add(nums[i]);
                        list.add(nums[j]);
                        list.add(nums[k] );
                        res.add(list);
                    }
                }
            }

        }

        return res;
    }
}
