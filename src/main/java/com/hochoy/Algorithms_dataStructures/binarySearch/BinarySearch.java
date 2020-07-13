package com.hochoy.Algorithms_dataStructures.binarySearch;

public class BinarySearch {


    // 1,3,5,7,9,11,13,15 , 17 ,19 ,21 ,23 ,25 ,27 ,29
    //   3

    public static void main(String[] args) {
        int[] nums = new int[]{1,3,5,7,9,11,13,15 , 17 ,19 ,21 ,23 ,25 ,27 ,29 };

        int target = 5;
        BinarySearch binarySearch = new BinarySearch();
        int i = binarySearch.binarySearch(nums,target);
        System.out.println(i == 2);

        System.out.println("--------");
        for (int k = 0; k < nums.length; k++) {
            int m = binarySearch.binarySearch(nums,2*k+1);
            System.out.println(m == k);
        }


    }
    int binarySearch(int[] nums ,int target){

        int left = 0;

        int right = nums.length - 1;

        while (left <= right){
            int mid = (right + left) /2;
            if (nums[mid] == target)
                return mid;
            else if (nums[mid] < target)
                left = mid +1;
            else if (nums[mid] > target)
                right = mid -1;
        }
        return  -1;
    }
}
