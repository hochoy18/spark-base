package com.hochoy.thread;

/**
 * join() 定义在Thread.java中。
 * join() 的作用：让“主线程”等待“子线程”结束之后才能继续运行。
 *
 * 不使用join时 的输出结果：
 * 先执行主线程再执行子线程
 * *******************************************************************
 <<<<<<<<finish>>>>>>>>>    main
 start ...Join_T1
 finish....Join_T1
 B   start ...joinB——T2
 B   finish....joinB——T2
 **********************************************************************
 *
 *
 * 加入join之后输出结果
 * **********************************************************************
 start ...Join_T1
 finish....Join_T1
 <<<<<<<<finish>>>>>>>>>    main
 B   start ...joinB——T2
 B   finish....joinB——T2
 * *********************************************************************
 */
public class JoinThread {
    public static void main(String[] args) throws Exception {
        Thread joinA = new JoinThreadA("Join_T1 ");
        Thread joinB = new JoinThreadB("joinB——T2 ");
        joinA.start();
        joinB.start();

//        joinA.join();

        System.out.println("<<<<<<<<finish>>>>>>>>>    "+ Thread.currentThread().getName());
//        joinB.join();
    }



}

class JoinThreadA extends Thread {
    public JoinThreadA(String name) {
        super(name);
    }

    @Override
    public void run() {
        System.out.println("start ..."+ this.getName());
        for (int i=0;i<100000;i++)
            ;
        System.out.println("finish...."+this.getName());
    }
}
class JoinThreadB extends Thread {
    public JoinThreadB(String name) {
        super(name);
    }

    @Override
    public void run() {
        System.out.println("B   start ..."+ this.getName());
        for (int i=0;i<100000;i++)
            ;
        System.out.println("B   finish...."+this.getName());
    }
}