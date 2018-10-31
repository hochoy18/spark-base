package com.hochoy.thread;

public class DaemonThread {
    public static void main(String[] args) {

    }
}

class DaemonThread2Main {
    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        System.out.println("main start  " + start);
        Thread t1 = new Thread(new DaemonThread2());
        /**
         * 守护线程创建的过程中需要先调用setDaemon方法进行设置,然后再启动线程.否则会报出IllegalThreadStateException异常.
         * 如果将以下
         * t1.setDaemon(true);
         * t1.start(); 互换位置则会报以下异常
         ********************************************************************************************************
         *
         *Exception in thread "main" java.lang.IllegalThreadStateException
         * at java.lang.Thread.setDaemon(Thread.java:1359)
         * at com.hochoy.thread.DaemonThread2Main.main(DaemonThread.java:21)
         *
         * *******************************************************************************************************
         */
        t1.setDaemon(true);
        t1.start();
        Thread.sleep(10);
        long finish = System.currentTimeMillis();
        System.out.println("main finish  " + finish);
        System.out.println("main interval   "+Long.toString( finish - start));
        System.out.println("用户线程退出");
    }
}

class DaemonThread2 implements Runnable {

    public void run() {
        try {
            /**
             * 在 DaemonThread2Main 中 对 该线程设置 setDaemon(true) 时
             * 以下
             * Thread.sleep(10000);
             * System.out.println("the current thread is ....."+ Thread.currentThread().getName());
             * 不会直接看到，线程会在后台运行
             *
             * 此时，输出为：
             *
             * *******************************************************************************************************
             * main start  1540977548873
             * main finish  1540977548885
             * main interval   12
             * 用户线程退出
             *
             * *******************************************************************************************************
             * 该线程不影响用户线程的运行，main的运行时间为 12 ms
             * 跟本线程的 sleep（10000）无关
             */
            Thread.sleep(10000);
            System.out.println("the current thread is ....."+ Thread.currentThread().getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


class DaemonThread1Main {
    public static void main(String[] args) throws Exception{
        Thread t =new DaemonThread1("DaemonThread1===========");
        t.setDaemon(true);
        t.start();
        System.out.println(t.getName() + "......... is or not daemon :"+t.isDaemon());
        Thread.sleep(1000);
        System.out.println("用户线程退出");
    }
}
class DaemonThread1 extends Thread{
    String name;
    public DaemonThread1(String name) {
        this.name = name;
    }
    public void run() {
        Thread t = new DaemonThread1_2("DaemonThread1_2");
        t.start();
        System.out.println(t.getName()+" is or not daemon thread : " + t.isDaemon());
    }
}
class DaemonThread1_2 extends Thread {
    public DaemonThread1_2(String name) {
        super(name);
    }

    @Override
    public void run() {
        System.out.println("the current thread is "+this.getName());
    }
}