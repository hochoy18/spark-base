package com.hochoy.concurrent;

import com.hochoy.utils.HochoyUtils;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ExecutorsTest {


    public static void main(String[] args) {
        ThreadPoolExecutor threadPoolExecutor  = test1( 5 );
        int activeCount0 = -1;
        int queueSize0 = -1;
        while (true){
            int activeCount = threadPoolExecutor.getActiveCount();
            int queueSize = threadPoolExecutor.getQueue().size();
            int maximumPoolSize = threadPoolExecutor.getMaximumPoolSize();
            int corePoolSize = threadPoolExecutor.getCorePoolSize();

           if (activeCount0 != activeCount || queueSize0 !=queueSize ){
               System.out.println("activeCount : " + activeCount);
               System.out.println("queueSize : " + queueSize);
               System.out.println("maximumPoolSize : " + maximumPoolSize);
               System.out.println("corePoolSize : " + corePoolSize);
               activeCount0 = activeCount;
               queueSize0 = queueSize;
               System.out.println("====================");
           }
        }
    }

    static ThreadPoolExecutor test1(int num ){
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, 3,
                1L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1)
//                , r -> {
//                    Thread thread = new Thread(r);
//                    return thread;
//                },new ThreadPoolExecutor.AbortPolicy()
        );





        java.util.concurrent.atomic.AtomicInteger ii = new java.util.concurrent.atomic.AtomicInteger(1);
        for (  int i = 1;i<= num ;i ++) {
            threadPoolExecutor.execute(()->{
                HochoyUtils.sleep(3000);
                System.out.println("-------------helloworld_00"+ ii.getAndAdd(1) +"---------------" + Thread.currentThread().getName());
            });
        }
//        threadPoolExecutor.execute(()->{
//            HochoyUtils.sleep(3000);
//            System.out.println("-------------helloworld_002---------------" + Thread.currentThread().getName());
//        });
//        threadPoolExecutor.execute(()->{
//            HochoyUtils.sleep(3000);
//            System.out.println("-------------helloworld_003---------------" + Thread.currentThread().getName());
//        });
//        threadPoolExecutor.execute(()->{
//            HochoyUtils.sleep(3000);
//            System.out.println("-------------helloworld_004---------------" + Thread.currentThread().getName());
//        });
//        threadPoolExecutor.execute(()->{
//            HochoyUtils.sleep(3000);
//            System.out.println("-------------helloworld_005---------------" + Thread.currentThread().getName());
//        });

        return threadPoolExecutor;
    }
}
