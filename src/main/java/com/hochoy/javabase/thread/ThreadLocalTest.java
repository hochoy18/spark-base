package com.hochoy.javabase.thread;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadLocalTest {
    static ThreadLocal<String> tl = new ThreadLocal<>();
    private static final Logger logger = LoggerFactory.getLogger(ThreadLocalTest.class);

    public static void main(String[] args) {

        tl.set("the value is set by Main");
        new Thread(() -> {
            tl.set("the value is set by T1");
            tl.set("the value is set by T1-1");
            System.out.println(Thread.currentThread().getName() + " -----  " + tl.get());
        }, "T1").start();

        new Thread(() -> {
            tl.set("the value is set by T2");
            System.out.println(Thread.currentThread().getName() + " -----  " + tl.get());
        }, "T2").start();

        System.out.println(Thread.currentThread().getName() + " -----  " + tl.get());
    }


}
