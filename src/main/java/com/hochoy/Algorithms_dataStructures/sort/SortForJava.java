package com.hochoy.Algorithms_dataStructures.sort;


/**
 * Describe:
 *
 * @author hochoy <hochoy18@sina.com>
 * @version V1.0.0
 * @date 2019/6/29
 */
public class SortForJava {

    static class QuickSort{


       static void quickSort(int[] arr ,int left,int right){
            int pivot = 0;
            if (left<right){
                pivot = partition(arr,left,right);
                quickSort(arr,left,pivot-1);
                quickSort(arr,pivot +1,right);
            }

        }

        /**
         *   我们从待排序的记录序列中选取一个记录(通常第一个)作为基准元素(称为key)key=arr[left]，然后设置两个变量，left指向数列的最左部，right指向数据的最右部。
         * @param arr 待排序的数组，
         * @param left
         * @param right
         * @return
         */
        static int partition(int[] arr , int left ,int right){
            System.out.println("left : "+left);
            int key = arr[left];
            while (left < right){
                while (left<right && arr[right] >=key ){
                    right --;
                }
                arr[left] = arr[right];
                while (left<right && arr[left] <=key){
                    left ++;
                }
                arr[right] = arr[left];

            }
            arr[left] = key;

            return left;
        }
    }
}
