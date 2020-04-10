package com.hochoy.thread.year2020;

public class MultiThread {
    public static void main(String[] args) {

        MyThread t = new MyThread();
        t.start();    //启动子线程
        //主线程继续同时向下执行
        MyThread t1 = new MyThread();
        t1.start();
        MyThread t11 = new MyThread();
        t11.start();
        System.out.println(Thread.currentThread().getName() + "-----------------------------------");
        for (int i = 0; i < 10000; i++) {
            System.out.print(i + "_\t\t");
        }
    }
    static class  MyThread extends Thread {
        public void run() {

            for (int i = 0; i < 10000; i++) {
                System.out.print(Thread.currentThread().getName() + "-" + i + " ");
            }
            System.out.println(Thread.currentThread().getName() + "-----------------------------------");
        }
    }
}
