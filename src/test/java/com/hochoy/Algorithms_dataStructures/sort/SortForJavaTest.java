package com.hochoy.Algorithms_dataStructures.sort;

import org.junit.Test;

import java.util.Arrays;

public class SortForJavaTest {

    @Test
    public void querySort(){
        int arr[]=new int[] {65,58,95,10,57,62,13,106,78,23,85};
        System.out.println("排序前："+ Arrays.toString(arr));

        SortForJava.QuickSort.quickSort(arr,0,arr.length-1);
        System.out.println("排序后："+Arrays.toString(arr));



    }

    @Test
    public void insertSort(){

        int[] nums = new int[]{9,8,7,6,5,4,3,2,1};
        System.out.println("排序前："+ Arrays.toString(nums));
        SortForJava.InsertSort.insertSort(nums);
        System.out.println("排序后："+ Arrays.toString(nums));

    }
}
