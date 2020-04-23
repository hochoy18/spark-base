package com.hochoy.concurrent;


import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.hochoy.utils.HochoyUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ScheduledTaskDemo {
    static Calendar calendar = Calendar.getInstance();

    public static void main(String[] args) {
//        schedule(1);
//        schedule(2);

        scheduleAtFixedRate(1);
//        scheduleAtFixedRate(2);
//        scheduleAtFixedRate(5);


//        scheduleWithFixedDelay(1);

//        submit();

    }


    static ScheduledExecutorService newDaemonSingleThreadScheduledExecutor(String threadName) {
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setDaemon(true).setNameFormat(threadName).build();
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1, threadFactory);
        // By default, a cancelled task is not automatically removed from the work queue until its delay
        // elapses. We have to enable it manually.
        executor.setRemoveOnCancelPolicy(true);
        return executor;
    }


    static void scheduleWithFixedDelay(int num) {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(num);
        long start = System.currentTimeMillis();
        executorService.scheduleWithFixedDelay(() -> {
            System.out.println(Thread.currentThread().getName() + " coming later first : " + (System.currentTimeMillis() - start));
            // 注意此处休眠时间为2s
            HochoyUtils.sleep(2000);
            // 第一个任务延迟0s执行，其余延迟为3s
        }, 0, 3, TimeUnit.SECONDS);
    }


    static void scheduleAtFixedRate(int num) {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(num);
        long start = System.currentTimeMillis();
//        long initialDelay,
//        long period,
        /**
         * 首次 执行 延迟 initialDelay 个时间单位，以后以固定速率  period 个时间单位 周期性的 执行
         */
        ScheduledFuture<?> scheduledFuture = executorService.scheduleAtFixedRate(() -> {
            System.out.println(Thread.currentThread().getName() + " coming later first : " + (System.currentTimeMillis() - start));
            HochoyUtils.sleep(2000);
            // 延迟0s执行，周期为3s
           // return 1000;
        }, 4, 3, TimeUnit.SECONDS);

//        try {
//
//            Object o = scheduledFuture.get();
//            System.out.println("xxxxx   "+o.toString());
//        }catch (Exception e ){
//            e.printStackTrace();
//        }



//        HochoyUtils.sleep(10000);
//        executorService.shutdown();
//        scheduledFuture.cancel(true);
//
//        System.out.println("isNull : "+ (scheduledFuture == null));
//        System.out.println("isCancelled : " +scheduledFuture.isCancelled());
//        System.out.println("isDone : "+scheduledFuture.isDone());
        /**
         * 首次 执行 延迟 initialDelay 个时间单位，以后以固定速率  period 个时间单位 周期性的 执行
         */
//        executorService.scheduleAtFixedRate(() -> {
//            System.out.println(Thread.currentThread().getName() + " coming later second : " + (System.currentTimeMillis() - start) );
//            sleep(1000);
//            // 延迟0s执行，周期为3s
//        }, 0, 2, TimeUnit.SECONDS);


    }


    static void submit() {
        List<Future<Integer>> resultList = new ArrayList<>();
        ExecutorService executorService = Executors.newCachedThreadPool();
        AtomicInteger ii = new AtomicInteger(0);
        for (int i = 0; i < 10; i++) {
            Future<Integer> future = executorService.submit(() -> {
                HochoyUtils.sleep(1000);
                int kk = ii.getAndAdd(1);
                System.out.printf("submit %s..... %n", kk);
                return 100 + kk;
            });
            resultList.add(future);
        }
        try {
            for (Future<Integer> future : resultList) {
                System.out.println(future.get());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //启动一次顺序关闭，执行以前提交的任务，但不接受新任务。如果已经关闭，则调用没有其他作用。
            executorService.shutdown();
        }
    }

    static void schedule(int num) {

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(num);
        System.out.println("num is " + num);
        long start = System.currentTimeMillis();
        System.out.println("first submit....");

        scheduledExecutorService.schedule(() -> {
            System.out.println(System.currentTimeMillis() - start);
            HochoyUtils.sleep(8000);

        }, 3, TimeUnit.SECONDS);
        System.out.println("second submit....");
        scheduledExecutorService.schedule(() -> {
            System.out.println(System.currentTimeMillis() - start);
        }, 3, TimeUnit.SECONDS);


    }


}

