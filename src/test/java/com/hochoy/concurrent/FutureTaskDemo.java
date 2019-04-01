package com.hochoy.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.ThreadFactory;

/**
 * Created by Hochoy on 2019/04/01.
 */
public class FutureTaskDemo<T> implements Runnable {

    private Callable<T> callable;
    T result;
    boolean isEnd = false;

    public FutureTaskDemo(Callable<T> callable) {
        this.callable = callable;
    }

    public void run() {
        try {
            result = callable.call();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            isEnd = true;
        }
        synchronized (this){
            this.notify();
        }
    }

    public T get() {
        if (isEnd) {
            return result;
        }else {
            System.out.println(Thread.currentThread().getName() +
                    "消费者进入 等待 ");
            synchronized (this){
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
        return result;
    }
}
