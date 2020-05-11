package com.hochoy.concurrent;


import java.util.concurrent.TimeUnit;

public class VolatileTest {

    private  /*volatile*/ static int a = 0;
    private  /*volatile*/ static int b = 0;
    private  /*volatile*/ static int x = 0;
    private  /*volatile*/ static int y = 0;
    static /*volatile*/ boolean isStop = false;

    public static void main(String[] args) throws InterruptedException {

        visibility();
        commandRearrangement();
    }


    private static void commandRearrangement() throws InterruptedException {

        int count = 0;
        while (true) {
            count++;

            a = 0;
            b = 0;
            x = 0;
            y = 0;

            Thread t = new Thread(() -> {
                a = 1;
                y = b;
            });
            t.start();
            Thread t1 = new Thread(() -> {
                b = 2;
                x = a;
            });
            t1.start();
            t.join();
            t1.join();
            System.out.printf("第%d 次 输出结果 :  a = %d, b = %d, x = %d, y = %d%n", count, a, b, x, y);
            if (x == 0 && y == 0) {
                // 第65990 次 输出结果 :  a = 1, b = 2, x = 0, y = 0
                break;
            }
        }
    }

    static void visibility() {
        Thread t1 = new Thread(() -> {
            while (!isStop) {
                //  logger.info("isStop:{}",isStop);
            }
        }, "loop");
        t1.start();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Thread t2 = new Thread(() -> {
            isStop = true;
            System.out.println(isStop);
        }, "update-value");
        t2.start();

        while (true) {
            if (!isStop) {
                System.out.println(">>>>>>>>>>>>> " + isStop);
            } else  {
                System.out.println("xxxx " + isStop);
            }
        }

    }


}
