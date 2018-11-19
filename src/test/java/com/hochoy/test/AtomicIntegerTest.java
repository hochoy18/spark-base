package com.hochoy.test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author :  hochoy
 * @version :  V1.0
 * @note :  TODO
 * @since :  Date : 2018年11月19日 10:58
 */

public class AtomicIntegerTest {
    static AtomicInteger count = new AtomicInteger(0);
    public static void main(String[] args) throws  Exception{
        for (int i = 0; i <10000 ; i++) {
            new Thread(){
                @Override
                public void run() {
                    count.getAndIncrement();
                }
            }.start();
        }
        Thread.sleep(1000l);
        System.out.println("count : "+ count);
    }
}

/**
 * 输出的结果为count: 9573，这个值不定，每次测试都可能不一样，很显然，100个线程跑++操作，结果并没有像预期的那样count: 10000。
 */
class IntCount {
    static int count = 0;

    public static void main(String[] args) {
        for (int i = 0; i < 10000; i++) {
            new Thread() {
                @Override
                public void run() {
                    count++;
                }
            }.start();
        }
        System.out.println("count : " + count);
    }
}

/**
 * volatile 修饰的变量能够在线程间保持可见性，能被多个线程同时读但是又能保证
 * 只被单个线程写，而且不会读到过期值（由java内存模型中的happen-before原则决定）
 * volatile 修饰字段的写入操作总是优先于读的操作，即使多个线程同时修改volatile
 * 变量的字段，总能保证获取到最新的值。
 * 输出： count : 9317    且总是不定
 *
 * volatile仅仅保证变量在线程间保持可见性，却依然不能保证非原子性的操作。
 */
class VolatileCount {
    static volatile int count = 0;

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 10000; i++) {
            new Thread() {
                @Override
                public void run() {
                    count++;
                }
            }.start();

        }
        System.out.println("count : " + count);
    }
}

