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
        bubbleSort(arr);

    }


    static int[] qSort(int [] arr){
        int K = arr[0];
        int left = 0;
        int right = arr.length;



        return arr;
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
