package com.hochoy.javabase.thread;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;


public class Test17 {

    static int counter = 0;
    static final Object lock = new Object();

    public static void main(String[] args) throws InterruptedException {
        int k = 10;
        while (k > 0) {
            Thread t1 = new Thread(() -> {
                synchronized (lock) {
                    for (int i = 0; i < 5000; i++) {
                        counter++;
                    }
                }
            }, "T1");

            Thread t2 = new Thread(() -> {
                synchronized (lock) {
                    for (int i = 0; i < 5000; i++) {

                        counter--;
                    }
                }
            }, "T2");
            t1.start();
            t2.start();

            t1.join();
            t2.join();
            k--;
            TimeUnit.MILLISECONDS.sleep(100);
            System.out.printf("counter %d : %s%n", k, counter);
            System.out.printf("counter %d : %s%n", k, counter);
            System.out.printf("counter %d : %s%n", k, counter);
        }


    }
}
