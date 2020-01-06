package com.hochoy.concurrent;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * Describe:
 *
 * @author hochoy <hochoy18@sina.com>
 * @version V1.0.0
 * @date 2019/3/28
 */
public class Test {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        boolean flag = test0000();
        System.out.println(flag);
        System.out.println(System.currentTimeMillis() - start + "   " + flag);
    }

    public static void test() {
        final CountDownLatch latch = new CountDownLatch(2);
        System.out.println("count........1 " + latch.getCount());
        new Thread(() -> {
            try {
                System.out.println(
                        "子线程  " + Thread.currentThread().getName()
                                + " is Running");
                Thread.sleep(3 * 1000);
                System.out.println(
                        "子线程 " + Thread.currentThread().getName()
                                + " run over");
                latch.countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        System.out.println("count........2 " + latch.getCount());
        new Thread() {
            @Override
            public void run() {
                try {
                    System.out.println(
                            "子线程  " + Thread.currentThread().getName()
                                    + " is Running");
                    Thread.sleep(3 * 1000);
                    System.out.println(
                            "子线程 " + Thread.currentThread().getName()
                                    + " run over");
                    latch.countDown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        System.out.println("count........3 " + latch.getCount());
        try {

            System.out.println("等待2个子线程执行完毕。。。。。。。。。");
            latch.await(1, TimeUnit.SECONDS);
            System.out.println("count........4 " + latch.getCount());
            System.out.println("2 个子线程已经执行完毕");
            System.out.println("继续执行主线程。。。。。。");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("count........5 " + latch.getCount());
    }

    void test001() {
        Callable<List<String>> listCallable = new Callable<List<String>>() {
            public List<String> call() throws Exception {
                return null;
            }
        };
        FutureTask<List<String>> task = new FutureTask<List<String>>(listCallable);
        new Thread(task).start();
    }


    static boolean test0000() {

        new Thread(() -> {
            try {
                Thread.sleep(1000 * 10);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();


        return true;

    }
}
