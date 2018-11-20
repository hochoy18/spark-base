package com.hochoy.concurrent;

import org.apache.avro.generic.GenericData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author :  hochoy
 * @version :  V1.0
 * @note :  TODO
 * @since :  Date : 2018年11月20日 11:42
 */

public class Test001 {
    public static void main(String[] args) throws Exception {
       test();

    }

    static void test1(){
        List<Integer> tmplist = Arrays.asList(new Integer[]{11, 12, 13, 14, 15, 16, 17, 18});
        CopyOnWriteArrayList<Integer> list = new CopyOnWriteArrayList<Integer>(tmplist);
        ExecutorService executor = Executors.newFixedThreadPool(10);
        executor.execute(new ReadThread(list));
        executor.execute(new WriteThread(list));
        executor.execute(new ReadThread(list));
        executor.execute(new WriteThread(list));
        executor.execute(new ReadThread(list));
        executor.execute(new WriteThread(list));
    }
    static void test() throws InterruptedException{
        List<String> a = new ArrayList<String>();
        a.add("a");
        a.add("b");
        a.add("c");

//        final ArrayList<String> list = new ArrayList<String>(a);
        final CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>(a);
        Thread t = new Thread(new Runnable() {
            int count = -1;
            public void run() {
                while (count < 100000) {
                    list.add(count++ + "");
                }
            }
        });
        t.setDaemon(true);
        t.start();
        Thread.currentThread().sleep(30);
        for (String s : list) {
            System.out.println("hashcode: "+list.hashCode());
            System.out.println(s);
        }
    }
}

class ReadThread implements Runnable {
    private List<Integer> list;

    public ReadThread(List<Integer> list) {
        this.list = list;
    }

    public void run() {
        for (Integer ele : list) {
            System.out.println("ReadThread:" + ele);
        }
    }
}

class WriteThread implements Runnable {
    private List<Integer> list;

    public WriteThread(List<Integer> list) {
        this.list = list;
    }


    public void run() {
        this.list.add(9);
    }
}