package com.hochoy.java8;

import org.junit.Test;

public class IntegerTest01 {
    @Test
    public void testInteger1(){
        // -XX:AutoBoxCacheMax=300
        // 默认 -128 ~ 127 之间，
        // 最大值可通过jvm参数 -XX:AutoBoxCacheMax   设置
        Integer i0111 =-129;
        Integer i0211 =-129;
        System.out.println(i0111==i0211);


        Integer i011 =-128;
        Integer i021 =-128;
        System.out.println(i011==i021);

        Integer i01 =-127;
        Integer i02 =-127;
        System.out.println(i01==i02);


        Integer i2 =100;
        Integer i3 =100;
        System.out.println(i2==i3);

        Integer i4 =128;
        Integer i5 =128;
        System.out.println(i4==i5);

        Integer i42 =300;
        Integer i52 =300;
        System.out.println(i42==i52);
    }

    @Test
    public void testInteger2(){
        Integer int1 = Integer.valueOf("100");
        Integer int2 = Integer.valueOf("100");
        System.out.println(int1 == int2); // true

        Integer int11 = Integer.valueOf("128");
        Integer int21 = Integer.valueOf("128");
        System.out.println(int11 == int21);//false
    }
    @Test
    public void testInteger3(){
        Integer int1 = new Integer("100");
        Integer int2 = new Integer("100");
        System.out.println(int1 == int2); //false

        Integer int11 = new Integer("128");
        Integer int21 = new Integer("128");
        System.out.println(int11 == int21);//false
    }
}
