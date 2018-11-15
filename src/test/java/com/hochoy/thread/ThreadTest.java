package com.hochoy.thread;

/**
 * @author :  hochoy
 * @version :  V1.0
 * @note :  TODO
 * @since :  Date : 2018年11月15日 13:50
 */

public class ThreadTest {

    public static void main(String[] args) {
//        Thread.currentThread.getStackTrace()
        StackTraceElement[] stes = Thread.currentThread().getStackTrace();
        for (StackTraceElement ste : stes) {
            System.out.println(ste.toString());
        }
    }
}