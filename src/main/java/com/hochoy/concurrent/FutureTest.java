package com.hochoy.concurrent;


import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class FutureTest {


    public static void main(String[] args) {
        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {

                System.out.print("Future running");
                int k = 0;
                while (k < 20) {
                    TimeUnit.MILLISECONDS.sleep(300);
                    System.out.print(".");
                    k++;
                }
                System.out.println();
                return 100;
            }
        };
        FutureTask<Integer> task = new FutureTask<>(callable);
        Thread thread = new Thread(task, "getResult");
        thread.start();

        System.out.println("main is running ......");

        try {
            Integer i = task.get();
            System.out.println("result : " + i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
