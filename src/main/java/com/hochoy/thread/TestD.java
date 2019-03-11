package com.hochoy.thread;

import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;

/**
 * Created by Hochoy on 2019/03/04.
 */
public class TestD {

    public static void main(String[] args) {
        new Thread(new Thread1()).start();
        try {
            Thread.sleep(5000);
        }catch (Exception e){}
        new Thread(new Thread2()).start();
    }


    static class Thread1 implements Runnable{
        public void run() {
            synchronized (TestD.class){
                System.out.println("enter t1....");
                System.out.println("t1 is waiting");
                try {
                    //调用wait()方法，线程会放弃对象锁，进入等待此对象的等待锁定池
                    TestD.class.wait();
                }catch (Exception e){
                    e.printStackTrace();
                }
                System.out.println("t1 is going on");
                System.out.println("t1 is over");
            }
        }
    }
    static class Thread2 implements Runnable{
        public void run() {
            synchronized (TestD.class){
                System.out.println("enter  t2.... ");
                System.out.println("t2 is sleeping ");
                // //notify方法并不释放锁，即使thread2调用了下面的sleep方法休息10ms，但thread1仍然不会执行
                //因为thread2没有释放锁，所以Thread1得不到锁而无法执行
                TestD.class.notify();
                try {
                    Thread.sleep(5000);
                }catch (Exception e){
                    e.printStackTrace();
                }
                System.out.println("T2 is going on ");
                System.out.println("T2 is over");
                TextInputFormat

            }
        }
    }



}
