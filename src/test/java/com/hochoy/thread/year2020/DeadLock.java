package com.hochoy.thread.year2020;

import com.hochoy.utils.HochoyUtils;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DeadLock {
    public static int flag = 1;

    static final Object o1 = new Object();
    static final Object o2 = new Object();

    public static void main(String[] args) {


        new Thread(()->{
            System.out.println(Thread.currentThread().getName());
            synchronized (o1){
                System.out.println("我是" + Thread.currentThread().getName() + "锁住了 o1....");
                HochoyUtils.sleep(3000);
                synchronized (o2){
                    System.out.println("我是" + Thread.currentThread().getName() + " 拿到 o1....");
                }
            }
        },"T1").start();
        HochoyUtils.sleep(1000);
        new Thread(()->{
            synchronized (o2){
                System.out.println("我是" + Thread.currentThread().getName() + "锁住了 o2....");
                HochoyUtils.sleep(3000);
                System.out.println(Thread.currentThread().getName() + "醒来->准备获取 o1");
                synchronized (o1){
                    System.out.println(Thread.currentThread().getName() + "拿到 o1");
                }
            }
        },"T2").start();

        Lock lock = new ReentrantLock();

//        lock.tryLock(10L, TimeUnit.SECONDS);



    }


}
