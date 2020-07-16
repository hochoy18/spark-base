package com.hochoy.jvm.classloader;

import java.util.concurrent.TimeUnit;

public class B {

    {
        System.out.println("init non-static codes");
    }
    static {
        System.out.println("init static codes");
    }
    public static String f1 = "static-fields";

    public  String f2 = "Non-static-fields";

    public static void m() {
        System.out.println("invoke static-method #m()");
    }
    public  void m1() {
        System.out.println("invoke Non-static-method #m1()");
    }

}

class A extends B {
    static {
        System.out.println("init static codes of SubClass@A");
    }
    {
        System.out.println("init non-static codes of SubClass@A");
    }
    public static void k() {
        System.out.println("invoke static-method #k() of SubClass@A");
    }
}
class F extends A{
    static {
        System.out.println("init static codes of SubClass@F");
    }
    {
        System.out.println("init non-static codes of SubClass@F");
    }
    public static void ff() {
        System.out.println("invoke static-method #k() of SubClass@F");
    }
}

class MainClass {
    static {
        System.out.println("init static codes of MainClass ... ");
    }
    {
        System.out.println("init non-static codes of MainClass ...");
    }
    public static void ff() {
        System.out.println("invoke static-method #ff() of MainClass ...");
    }
    public   void kk() {
        System.out.println("invoke Non-static-method #ff() of MainClass ...");
    }
    public static void main(String[] args) {

        new F();
        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new MainClass().kk();
    }
}


class T{
    static {
        i = 0;
        System.out.println("var i is init ....");
    }
    static int i = 1;
}

class TT{
    public static void main(String[] args) {
        new T();
    }
}