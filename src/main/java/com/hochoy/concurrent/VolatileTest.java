package com.hochoy.concurrent;

public class VolatileTest {


    private static int a = 0;
    private static int b = 0;
    private static int x = 0;
    private static int y = 0;

    //    private static Thread t1 = new Thread(() -> {
//        a = 1;
//        y = b;
//    });
//    private static Thread t2 = new Thread(() -> {
//        b = 2;
//        x = a;
//    });
    public static void main(String[] args) throws InterruptedException {

        int count =0;
        while (true) {
            count ++;

            a = 0;
            b = 0;
            x = 0;
            y = 0;

            Thread t = new Thread(() -> {
                a = 1;
                y = b;
            });
            t.start();
            Thread t1 =   new Thread(() -> {
                b = 2;
                x = a;
            });
            t1.start();
            t.join();
            t1.join();
            System.out.printf("第%d 次 输出结果 :  a = %d, b = %d, x = %d, y = %d%n",count, a, b, x, y);
            if (x == 0 && y == 0){
                // 第65990 次 输出结果 :  a = 1, b = 2, x = 0, y = 0
                break;
            }
        }
    }


}

