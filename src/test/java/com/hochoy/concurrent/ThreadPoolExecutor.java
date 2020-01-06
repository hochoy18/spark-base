package com.hochoy.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ThreadPoolExecutor {
    static final int CORE_POOL_SIZE = 5;
    static final int MAXIMUM_POOL_SIZE = 5;
    static final long KEEP_ALIVE_TIME = 0L;
    static final int QUEUE_CAPACITY = 500;
    public static void main(String[] args) {
        java.util.concurrent.ThreadPoolExecutor threadPool =
                new java.util.concurrent.ThreadPoolExecutor(
                        CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(QUEUE_CAPACITY), new java.util.concurrent.ThreadPoolExecutor.AbortPolicy());


        List<String> list = new ArrayList<>();
        list.add("11111");
        list.add("22222");
        list.add("33333");
        list.add("44444");
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                long duration = duration(list);
                System.out.println(duration);
            }
        });


        List<String>  list1 = new ArrayList<>();
        list1.add("9999-------------");
        list1.add("8888-------------");
        list1.add("7777-------------");
        list1.add("66666------------");
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                long duration = duration(list1);
                System.out.println(duration);
            }
        });
    }

    static long duration(List<String> list ){
        long start = System.currentTimeMillis();
        for (String ele : list) {
            try {
                System.out.println(ele + " start....");
                Thread.sleep(2 * 1000);
                System.out.println(ele + " end.");
            }catch (InterruptedException e ){
                e.printStackTrace();
            }
        }


        return System.currentTimeMillis() - start;
    }
}
