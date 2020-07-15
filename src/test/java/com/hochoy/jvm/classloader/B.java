package com.hochoy.jvm.classloader;

public class B {
    static {
        System.out.println("init B");
    }
    public static int f = 100;
    {
        System.out.println("init xxxx");
    }
    public static void m() {
        System.out.println("invoke m");
    }
}

class A extends B {
    static {
        System.out.println("init A");
    }
    {
        System.out.println("init A xxxxx");
    }
}