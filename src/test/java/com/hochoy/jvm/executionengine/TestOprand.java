package com.hochoy.jvm.executionengine;

/**
 * Created by Hochoy on 2019/08/18.
 */
public class TestOprand {


    public static void main(String[] args) {

        int a =1 ;
        int b =2;
        int c = add(a,b);
        System.out.println(c);
    }
    static int add(int a,int b){
        int c = a + b;
        return c;
    }


}
