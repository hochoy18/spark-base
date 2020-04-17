package com.hochoy.thread.year2020;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * https://www.cnblogs.com/ConstXiong/p/12009144.html
 */
public class TestNotifyNotifyAll {


    private static Object obj = new Object();

    public static void main(String[] args) {

        testBlock();

//        testNotify();
    }


    static void testBlock(){
        Counter c = new Counter();

        Thread t1 = new Thread(new Runnable() {
            public void run() {
                c.increase();
            }
        }, "t1线程");
        t1.start();

        Thread t2 = new Thread(new Runnable() {
            public void run() {
                c.increase();
            }
        }, "t2线程");
        t2.start();

       try {
           Thread.sleep(100); // 确保 t2 run已经得到执行
       }catch (Exception e ){
           e.printStackTrace();
       }
        Thread.State state = t2.getState();
        System.out.println(state);


    }

    static void testNotify(){

        //测试 RunnableImplA wait()
        Thread t1 =   new RunnableImplA(obj,"t1",0);
        Thread t2 = new RunnableImplA(obj,"t2",0);
        t1.start();
        t2.start();

//RunnableImplB notify()
//        Thread t3 = new RunnableImplB(obj,"T3",3);
//        t3.start();
//
//
        Thread t4 = new RunnableImplB(obj,"T4>>>>>>>>>>",6);
        t4.run();
//        t4.start();

        Thread t6 = new RunnableImplC(obj,"T5>>>>>>>>>>>>>",10);
        t6.start();
        try {
            Thread.sleep(100 * 1000);
        }catch (Exception e ){
            e.printStackTrace();
        }
    }

}

class RunnableImplA extends Thread{

    private Object obj;
    private int waitSecond;
    private static final Logger logger = LoggerFactory.getLogger(TestNotifyNotifyAll.class);
    public RunnableImplA(Object obj,String name,int waitSecond) {
        super("RunnableImpl-----A-" + name);
        this.obj = obj;
        this.waitSecond = waitSecond;
    }

    @Override
    public void run() {
        System.out.printf("run on RunnableImplA %s .....%n",currentThread().getName());
        synchronized (obj){
            System.out.printf("obj to wait on RunnableImplA %s...%n" ,currentThread().getName());
            try {
//                obj.wait();
                logger.info("{} entered try",currentThread().getName());
                obj.wait(waitSecond * 1000);
            }catch (InterruptedException e ){
                e.printStackTrace();
            }
            System.out.printf("obj continue to run onRunnableImplA %s...%n" ,currentThread().getName());
        }
    }
}

class RunnableImplB extends Thread{
    private Object obj;
    private int sleepSec;
    private static final Logger logger = LoggerFactory.getLogger(TestNotifyNotifyAll.class);


    public RunnableImplB(Object obj,String name ,int sleepSec ) {
        super("RunnableImpl-----B-" + name);
        this.obj = obj;
        this.sleepSec = sleepSec;
    }

    @Override
    public void run() {
        System.out.println("----------run on RunnableImplB ... ");
        System.out.println("---------sleep "+ sleepSec +" sec");
        try {
            logger.info("{} entered try",currentThread().getName());
            Thread.sleep(sleepSec * 1000);
        }catch (Exception  e ){
            e.printStackTrace();
        }
        synchronized (obj){
            logger.info("{} notify obj on ",currentThread().getName());
            obj.notify();
        }
    }
}

class RunnableImplC extends Thread{
    private Object obj;

    private int sleepSec;
    public RunnableImplC(Object obj,String name,int sleepSec) {
        super("RunnableImpl-----C- " + name);
        this.obj = obj;
        this.sleepSec = sleepSec;
    }

    @Override
    public void run() {
        System.out.println("CCC==============       run on RunnableImplC ...");
        System.out.println("CCC==============       sleep "+sleepSec+" sec ");
        try {
            Thread.sleep(sleepSec * 1000);
        }catch (Exception e ){
            e.printStackTrace();
        }
        synchronized (obj){
            System.out.println("CCC==============       notifyAll obj on RunnableImplC ...");
            obj.notifyAll();
        }
    }
}


class RunnableImplD extends Thread {

    private Object obj;

    private int sleepSec;
    public RunnableImplD(Object obj,String name,int sleepSec) {
        super("RunnableImpl-----D- " + name);
        this.obj = obj;
        this.sleepSec = sleepSec;
    }

    @Override
    public void run() {
        try {
            synchronized(obj)
            {
                while (true)
                {
                    obj.wait();
                }
            }
        }catch (Exception e ){
            e.printStackTrace();
        }
    }
}

class Counter {
    int counter;
    public synchronized void increase() {
        counter++;
        try {
            Thread.sleep(30 * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}















