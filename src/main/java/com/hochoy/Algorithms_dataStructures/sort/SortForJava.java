package com.hochoy.Algorithms_dataStructures.sort;


import java.util.Arrays;
import java.util.concurrent.TimeUnit;

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


    static class BubbleSort{
        //          0  1  2  3  4  5  6  7  8  9  10
        //          3  6  9  6  3  2  9  7  0  1   4  => 0 1 2   3 3 4 6 6   7 9 9
        //0->10     3  6  6  3  2  9  7  0  1  4   9
        //0->9      3  6  3  2  6  7  0  1  4  9   _
        //0->8      3  3  2  6  6  0  1  4  7  _   _
        //0->7      3  2  3  6  0  1  4  6  _  _   _
        //0->6      2  3  3  0  1  4  6  _  _  _   _
        //0->5      2  3  0  1  3  4  _  _  _  _   _
        //0->4      2  0  1  3  3  _  _  _  _  _   _
        //0->3      0  1  2  3  _  _  _  _  _  _   _
        //0->2      0  1  2  _  _  _  _  _  _  _   _
        //0->1      0  1  _  _  _  _  _  _  _  _   _

        /**
         * (1)由此可见：N个数字要排序完成，总共进行N-1趟排序，每i趟的排序次数为(N-i)次，所以可以用双重循环语句，【【【外层控制循环多少趟，内层控制每一趟的循环次数】】】
         *
         * (2)冒泡排序的优点：每进行一趟排序，就会少比较一次，因为每进行一趟排序都会找出一个较大值。
         * 如上例：
         * 第一趟比较之后，排在最后的一个数一定是最大的一个数，
         * 第二趟排序的时候，只需要比较除了最后一个数以外的其他的数，同样也能找出一个最大的数排在参与第二趟比较的数后面，
         * 第三趟比较的时候，只需要比较除了最后两个数以外的其他的数，
         * 以此类推……也就是说，没进行一趟比较，每一趟少比较一次，一定程度上减少了算法的量。
         *
         * x = x ^ y ， y = x ^ y ， x = x ^ y
         * @param nums
         */
        static void bubbleSort(int[] nums) {
            if (nums == null || nums.length < 2)
                return;

            for (int i = 0; i < nums.length - 1; i++) {
                for (int j = 0; j < nums.length - i - 1; j++) {
                    if (nums[j] > nums[j + 1]) {
//                        int tmp = nums[j];
//                        nums[j] = nums[j + 1];
//                        nums[j + 1] = tmp;
                        nums[j] =   nums[j] ^ nums[j+1];
                        nums[j+1] = nums[j] ^ nums[j+1];
                        nums[j] =   nums[j] ^ nums[j+1];
                        ArraysUtil.print(nums,3000);
                    }
                }
                System.out.println();
            }
        }

    }

    static class ArraysUtil{
        static void print(int[] nums,int sleepMs){
            System.out.println(Arrays.toString(nums));
            try {
                TimeUnit.MILLISECONDS.sleep(sleepMs);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

