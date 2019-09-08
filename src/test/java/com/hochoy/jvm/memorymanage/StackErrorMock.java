package com.hochoy.jvm.memorymanage;

import java.util.ArrayList;

public class StackErrorMock {
    private static int index = 1;

    public void call(){
        index++;
        call();
    }

    public static void main(String[] args) {
//        oom();
        stackOverflowError();
    }
    static void oom(){
        // -Xms20m -Xmx20m
        ArrayList list = new ArrayList();
        while (true) {
            list.add(new StackErrorMock());
        }
    }
    static void stackOverflowError(){
        //  -Xss128k
        StackErrorMock mock = new StackErrorMock();
        try {
            mock.call();
        }catch (Throwable e){
            System.out.println("Stack deep : "+index);
            e.printStackTrace();
        }
    }

}