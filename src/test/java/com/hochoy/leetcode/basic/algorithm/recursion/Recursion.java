package com.hochoy.leetcode.basic.algorithm.recursion;

import org.junit.Test;

/**
 * https://www.iteye.com/blog/sky-xin-2297246
 */
public class Recursion {


    @Test
    public void test_sum_1_to_100() {
        System.out.println(multiRecursion(6));
        //System.out.println(sumRecursion(100));
    }

    public int sum_1_to_100(int num) {
        int sum = 0;
        while (num >= 0) {
            sum += (num--);
        }
        return sum;
    }

    public int sumRecursion(int num) {
        if (num == 0)
            return 0;
        else
            return num + sumRecursion(--num);
    }

    public int multiRecursion(int num) {
        if (num == 1)
            return 1;
        else if (num == 0)
            return 0;
        else return num * multiRecursion(--num);
    }

    @Test
    public void adjacentNumSumTest() {
        for (int k = 0; k <= 40; k++) {
            int i = adjacentNumSum(k);
            System.out.println(i);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }

    public int adjacentNumSum(int num) {
        //    1、   1、   2、   3、   5、   8、   13、   21、   34
        //    0     1     1+1   1+2   3+2   5+3   8+5    31+8   21+13
        //    0     1     2     3     4     5     6       7     8
        if (num < 0 || num == 0 || num == 1)
            return 1;
        else
            return adjacentNumSum(--num) + adjacentNumSum(--num);

    }


    // 问题：N级台阶（比如100级），每次可走1步或者2步，求总共有多少种走法？
    //  0 :1
    //  1 :1
    //  2 :2 (1+1 /2)         1阶 * ( 2 / 1) 步, 2阶 * ( 2 / 2 )步 + 1阶 * (2 % 2)步 [[[
    //  3 :  (1+1+1 ， 2 * 1 + 1，1 + 2      1 * （3/1），
//    public int nStep(int num) {
//        if (num < 0) {
//            return 0;
//        }
//        if (num <=1 )
//            return 1;
//        else
//
//
//    }


    /**
     *  0  - num 的整数和
     * @param num
     */
    public int getAddNum(int num){
        if (num <= 0 )
            return 0;
        else return num + getAddNum( --num );
    }
    @Test
    public void getAddNumTest(){
        System.out.println(getAddNum(10));
        System.out.println(getAddNum(100));
        System.out.println(getAddNum(1000));
    }

    /**
     * 十进制转成二进制
     * @param num
     * @return
     */
    StringBuilder sb = new StringBuilder();
    public String decimalToBinary(int num){
        if (num == 0 || num ==1 )
            sb.append(num);
        else{



//            System.out.println(Math.log(Math.E));
//            for (int i =1;i <= 64 ;i++){
//                double log =  (Math.log(i) / Math.log(2));
//
//                System.out.printf("i:%d, log:%f %n",i,log);
//            }
//            String s = decimalToBinary(--num);
//            sb.append(s);
        }

        return sb.reverse().toString();
    }
    @Test
    public void  decimalToBinary(){
        decimalToBinary(3);
//        System.out.println(decimalToBinary(0)); // 0
//        System.out.println(decimalToBinary(1)); // 1
//        System.out.println(decimalToBinary(2)); // 10
//        System.out.println(decimalToBinary(3)); // 11
//        System.out.println(decimalToBinary(4)); // 100
//        System.out.println(decimalToBinary(5)); // 101
//        System.out.println(decimalToBinary(8)); // 1000
//        System.out.println(decimalToBinary(10));// 1010

    }
}
