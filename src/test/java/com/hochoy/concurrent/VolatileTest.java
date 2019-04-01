package com.hochoy.concurrent;

import jodd.datetime.TimeUtil;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Describe:
 *
 * @author hochoy <hochoy18@sina.com>
 * @version V1.0.0
 * @date 2019/3/29
 */
class V_Test1 {
    boolean status = false;

    public void changeStatus() {
        status = true;
    }

    public void run() {
        if (status) {
            System.out.println("running ..........");
        }
    }
}


class VisibleTest {
    private static volatile boolean is = true;
    private static int i = 0;
    static final Random random = new Random();

    public static void main(String[] args) {
        visibleTest1();
//        visibleTest2();
//        visibleTest3();

    }


    static void visibleTest4(){

    }

    static void visibleTest3() {
        class VT {
            int a = 0;
            boolean flag = false;

            void writer() {
                System.out.println("write...." + random + "   " + random.nextInt());
                a = 1;
                flag = true;
            }

            void reader() {
                System.out.println("reader...." + random + "   " + random.nextInt());
                System.out.println();
                if (flag) {
                    int m = a * a;
                    System.out.println("m ...." + m);
                }
            }
        }
        VT test = new VT();
        new Thread(() -> test.writer()).start();
        new Thread(() -> test.reader()).start();
    }

    static void visibleTest2() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + "   " + i);
                i++;
                System.out.println(Thread.currentThread().getName() + "   " + i);
            }
        }, "T-1").start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + "   " + i);
                i++;
                System.out.println(Thread.currentThread().getName() + "   " + i);
            }
        }, "T-2").start();
        System.out.println(i);
    }

    static void visibleTest1() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (is) {
                    i++;
//                    System.out.println("i++   "+ i++);
                }
            }
        }).start();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        is = false;
        System.out.println("is is set false........");
    }
}


public class VolatileTest {
}



