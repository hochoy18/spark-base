package com.hochoy.thread;

/**
 * 程序创建了名为 Hello1  Hello1222222222    Hello1!!!!!!!!!!!!!!!!!!
 * 的三个进程，三个进程的优先级一次降低，没有调用yield() 方法时，线程会
 * 按优先级顺序执行，
 * 调用yield() 方法时，三个线程会一次打印
 *
 *
 * Thread.yield( )方法：
 * 使当前线程从执行状态（运行状态）变为可执行态（就绪状态）。cpu会从众多的可执行态里选择，
 * 也就是说，当前也就是刚刚的那个线程还是有可能会被再次执行到的，并不是说一定会执行其他线程而该线程在下一次中不会执行到了。
 */
public class YieldThread extends Thread {
    public YieldThread(String name) {
        super(name);
    }

    @Override
    public void run() {
        for (int i = 0; i <= 20; i++) {
            System.out.println("" + this.getName() + "-----------" + i);
            //可以把此处yield 注释掉查看输出效果
            Thread.yield();
        }
    }
}

class Run {
    public static void main(String[] args) {
        YieldThread yt1 = new YieldThread("Hello1");
        YieldThread yt2 = new YieldThread("Hello1222222222");
        YieldThread yt3 = new YieldThread("Hello1!!!!!!!!!!!!!!!!!!");
        yt1.setPriority(Thread.MAX_PRIORITY);
        yt2.setPriority(Thread.NORM_PRIORITY);
        yt3.setPriority(Thread.MIN_PRIORITY);
        yt1.start();
        yt2.start();
        yt3.start();
    }
}