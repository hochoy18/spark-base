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


    static class InsertSort {



        //idx: 0  1  2  3   4   ...
        //     9  8  7  6   5   4  3  2  1
        //     6  7  8  9   五  tmp = 5 ; i = 4 , j = 4 ===> nums[j-1] = 9 > tmp { nums[j] = nums[j-1] ; j = j-1 = 3}
        //     6  7  8  8   9    xxxxxxx;         j = 3 ===> nums[j-1] = 8 > tmp { nums[j] = nums[j-1] ; j = j-1 = 2 }
        //     6  7  7  8   9    xxxxxxx;         j = 2 ===> nums[j-1] = 7 > tmp { nums[j] = nums[j-1] ; j = j-1 = 1 }

        //     6  6  7  8   9    xxxxxxx;         j = 1 ===> nums[j-1] = 6 > tmp { nums[j] = nums[j-1] ; j = j-1 = 0 }
        //     5  6  7  8   9    xxxxxxx;         j = 0


        //                                                     0  1  2  3  4  5   6 对“七” 进行插入排序
        //                                                     6  7  7  8  8  9  七      =====> tmp = 7 ; i = 6 ;j = 6
        // nums[j-1] = 9  > tmp,nums[j] = nums[j-1];j--  ===>  6  7  7  8  8  9   9      =====> tmp = 7  ;j = 5
        // nums[j-1] = 8  > tmp,nums[j] = nums[j-1];j--  ===>  6  7  7  8  8  8   9      =====> tmp = 7  ;j = 4
        // nums[j-1] = 8  > tmp,nums[j] = nums[j-1];j--  ===>  6  7  7  8  8  8   9      =====> tmp = 7  ;j = 3
        // nums[j-1] = 7  = tmp,nums[j] = tmp ;break;    ===>  6  7  7  7  8  8   9
        //
        static void insertSort(int[] nums) {
            int tmp;
            for (int i = 1; i < nums.length; i++) {
                if (nums[i - 1] > nums[i]) {
                    tmp = nums[i];
                    int j = i;
                    while (j >= 0) {
                        if (j > 0 && nums[j - 1] > tmp) {
                            nums[j] = nums[j - 1];
                        } else {
                            nums[j] = tmp;
                        }
                        j--;
                    }
                }
            }
        }
    }



    static class BucketSort{


        //          0  1  2  3  4  5  6  7  8  9 10  11
        //          5  3  5  2  8
        //          0  0  0  0  0  0  0  0  0  0  0   0
        //



        static void bucketSort(int[] nums){

        }
    }
}
