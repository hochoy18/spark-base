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
    public void bubbleSort(){

        int arr[]=new int[] {65,58,95,10,57,62,13,106,78,23,85};
        System.out.printf("排序前：\n%s\n", Arrays.toString(arr));
        System.out.println("--------------------------------------------");
        SortForJava.BubbleSort.bubbleSort(arr);
        System.out.println("--------------------------------------------");
        System.out.printf("排序后：\n%s\n", Arrays.toString(arr));

    }


    @Test
    public void mergeSort(){

        int arr[]=new int[] {65,58,95,10,57,62,13,106,78,23,85};
//
//        int len = arr.length;
//        for (int i = 0; i <len -1 ; i++) {
//            for (int j = i; j < len; j++) {
//                System.out.println("\n--------------------------------------------\n");
//                int[] tmp = Arrays.copyOfRange(arr,0,arr.length);
//                SortForJava.ArraysUtil.print(tmp,500);
//                int mid = i + ((i + j) >> 1);
//                SortForJava.MergeSort.merge(tmp,i,mid,j);
//                SortForJava.ArraysUtil.print(tmp,3000);
//
//            }
//        }


        System.out.printf("排序前：\n%s\n", Arrays.toString(arr));
        System.out.println("--------------------------------------------");
        SortForJava.MergeSort.mergeSort(arr);
        System.out.println("--------------------------------------------");
        System.out.printf("排序后：\n%s\n", Arrays.toString(arr));

    }



    @Test
    public void insertSort(){

        int[] nums = new int[]{9,8,7,6,5,4,3,2,1};
        System.out.println("排序前："+ Arrays.toString(nums));
        SortForJava.InsertSort.insertSort(nums);
        System.out.println("排序后："+ Arrays.toString(nums));

    }
}
