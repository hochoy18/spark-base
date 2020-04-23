package com.hochoy.concurrent;

import com.hochoy.utils.HochoyUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SquareCalculator {

    private ExecutorService executorService =
            Executors.newSingleThreadExecutor();

    public static void main(String[] args) {
        Future<Integer> future = new SquareCalculator().calculate(10);

        int flag = 0;
        while (! future.isDone()){
            System.out.println("Calculating ...");
            flag ++;
//            if (flag == 2){
//                future.cancel(true);
//            }
            HochoyUtils.sleep(2000);
        }

        try {
            Integer integer = future.get();
            System.out.println(integer);
        }catch (Exception e ){
            e.printStackTrace();
        }
        boolean cancelled = future.isCancelled();
        System.out.println(cancelled);
        boolean done = future.isDone();
        System.out.println(done);
        System.out.println(future == null);
        future = null;


    }
    public Future<Integer> calculate(Integer input ){
        return executorService.submit(()->{
            Thread.sleep(10000);
            return input * input;
        });
    }
}
