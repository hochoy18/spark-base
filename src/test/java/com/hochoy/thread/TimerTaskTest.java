package com.hochoy.thread;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Describe:
 *
 * @author hochoy <hochoy18@sina.com>
 * @version V1.0.0
 * @date 2019/4/2
 */
public class TimerTaskTest {


}

/**
 * TimerTask.java：主要为任务的具体内容。 {@link java.util.TimerTask} 其实现了 Runnable 接口
 * 并提供一下几个方法：
 * {@link TimerTask#TimerTask()}
 * {@link TimerTask#run()}
 * {@link TimerTask#scheduledExecutionTime()}
 * {@link TimerTask#cancel()};
 */

/**
 *
 *Timer.java中含有3个类：{@link Timer}、TimerThread、TaskQueue。
 *三者关系为：TaskQueue中存放一些列将要执行的TimerTask，以数组的形式存放，下标约小（注：下标为0不处理，即使用的最小下标为1），则表明优先级越高。
 *
 * TimerThread为Thread的扩展类，会一直从TaskQueue中获取下标为1的TimerTask进行执行。并根据该TimerTask是否需要重复执行来决定是否放回到TaskQueue中。
 * Timer用于配置用户期望的任务执行时间、执行次数、执行内容。它内部会配置TimerThread、TaskQueue。
 *
 */

class TimerTest {
    static String sdate = "2019-04-02 10:41:00";
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        executionFromNowRepeat();
    }

    static void executionOnce() {
        /**
         *
         * schedule(TimerTask task, Date time)
         * 指定time执行 一次task任务
         */

        System.out.println("start........");
        System.out.println("now time : " + sdf.format(new Date()));
        try {
            Date date = sdf.parse(sdate);
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                int i = 0;

                public void run() {
                    System.out.println("execution time : " + sdf.format(new Date()));
                    System.out.println("system is running  i = " + ++i);
                }
            }, date);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void executionRepeat() {
        /**
         *
         * schedule(TimerTask task, Date firstTime, long period)
         * 以指定的firstTime 为起始时间，每个period 毫秒 重复执行 task 任务
         *
         */
        System.out.println("start........");
        System.out.println("now time : " + sdf.format(new Date()));
        try {
            Date date = sdf.parse(sdate);
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                int i = 0;

                public void run() {
                    System.out.println("execution time : " + sdf.format(new Date()));
                    System.out.println("system is running  i = " + ++i);
                }
            }, date, 1000L * 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void executionFromNowOnce() {
        /**
         * schedule(TimerTask task, long delay)
         * 启动定时器之后 延迟 delay 毫秒 执行一次task 任务，
         */
        System.out.println("start........");
        System.out.println("now time : " + sdf.format(new Date()));
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            int i = 0;

            public void run() {
                System.out.println("execution time : " + sdf.format(new Date()));
                System.out.println("system is running  i = " + ++i);
            }
        }, 10 * 1000L);

    }

    static void executionFromNowRepeat() {
        /**
         * schedule(TimerTask task, long delay, long period)
         * 启动定时器后，延迟 delay 毫秒 每个 period 重复执行
         */
        System.out.println("start........");
        System.out.println("now time : " + sdf.format(new Date()));
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            int i = 0;

            public void run() {
                System.out.println("execution time : " + sdf.format(new Date()));
                System.out.println("system is running  i = " + ++i);
            }
        }, 10 * 1000L, 1 * 1000L);
    }


}

