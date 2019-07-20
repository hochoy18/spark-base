package com.hochoy.Algorithms_dataStructures.sort;


/**
 * Describe:
 *
 * @author hochoy <hochoy18@sina.com>
 * @version V1.0.0
 * @date 2019/6/29
 */
public class SortForJava {

    public static void main(String[] args) {
        int[] arr = {2, 3, 1, 5, 2, 8, 3, 91, 2, 4, 3};
//        bubbleSort(arr);
//        int i = quickSortPart1(arr);
//        System.out.println(i);
    }   


    static int quickSortPart1(int [] arr,int left ,int right){
        int begin = left;
        int end = right;
        int key = right;
        while (begin<end){
            while (begin < end && arr[begin] <= arr[key]){
                ++ begin;
            }
            while (begin<end && arr[end] >= arr[key]){
                -- end;
            }
            int tmp = arr[begin];
            arr[begin] = arr[end];
            arr[end] = tmp;
        }
        int tmp = arr[begin];
        arr[begin] = arr[end];
        arr[end] = tmp;
        return begin;

    }

    static int [] quickSort1(int arr[],int left,int right){
//        if (left>=right){
//            return ;
//        }
//        if((right - left +1 )< 10)
//
        return null;
    }




    static int[] bubbleSort(int[] arr) {
        int len = arr.length;
        int mid;
        for (int i = 0; i < len-1; i++) {
            for (int j = 0; j < len-i-1; j++) {
                if (j ==9){
                    for (int k=0;k<len-1;k++){
                        System.out.print(arr[k]+ " * ");
                    }
                }
                if(arr[j]>arr[j+1]){
                    mid = arr[j];
                    arr[j] = arr[j+1];
                    arr[j+1] = mid;
                }
                for (int k=0;k<=len-1;k++){
                    System.out.print(arr[k]+ ", ");
                }
                System.out.println();
            }
        }
        return arr;
    }


}
