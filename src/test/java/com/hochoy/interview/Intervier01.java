package com.hochoy.interview;

import org.junit.Test;

import java.util.ArrayList;

public class Intervier01 {

    public static void main(String[] args) {
        intAndInteger();
    }

    static void intAndInteger() {


        Integer ii = new Integer(10);
        ii = 5;
        System.out.println(ii);

//        int a = 100;
//        int b = 100;
//        System.out.println(a == b);
//        Integer integerA = new Integer(100);
//        Integer integerB = new Integer(100);
//        System.out.println(integerA.equals(integerB));
//        System.out.println(integerA == integerB);
//        System.out.println(integerA == a);

    }


    @Test
    public void arrayTest() {
        int[] res1 = new int[5];
        for (int i = 0; i < res1.length; i++) {
            System.out.print(res1[i] + "\t");// 0,0,0,0,0
        }

        System.out.println("\n" + res1.length);// 5


        String[] res0 = new String[10];
        for (int i = 0; i < 5; i++) {
            res0[i] = "11111----" + i;
        }
        System.out.println(res0.length); // 10
        for (int i = res0.length - 1; i >= 0; i--) {

            System.out.print(res0[i] + "\t"); // null	null	null	null	null	11111----4	11111----3	11111----2	11111----1	11111----0
        }

    }

    @Test
    public void arrayTest1(){
        String[] res0 = new String[5];
        for (int k = 0; k < res0.length; k++) {
            System.out.println(res0[k]);
        }
        for (int i = 0; i < 5;  ) {
            res0[i++] = null;//"kkk  :  " + ++i;
        }
        for (int k = 0; k < res0.length; k++) {
            System.out.println(res0[k]);
        }

        res0[5 ] = "xxxxxxxx  5 ";
        for (int k = 0; k < res0.length; k++) {
            System.out.println(res0[k]);
        }

    }

    @Test
    public void ArrayListTest(){
        ArrayList<String > res0 = new ArrayList<>(10);

        System.out.println(res0.size());


        res0.add(null);
        res0.add(null);
        res0.add(null);

//        res0.add("0");
//        res0.add("01");
//        res0.add(1,"1");
//        res0.add(5,"5");
//        res0.add(30,"30");

        System.out.println(res0);

    }
}
