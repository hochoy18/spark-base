package com.hochoy.test;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Describe:
 *
 * @author hochoy <hochoy18@sina.com>
 * @version V1.0.0
 * @date 2019/2/12
 */
public class ConcurrentLinkedQueueTest {
    public static void main(String[] args) {
        ConcurrentLinkedQueue queue = new ConcurrentLinkedQueue();
        System.out.println(queue.isEmpty());
        queue.offer("hahaha");
        queue.add("abd");
        System.out.println(queue.isEmpty());
        System.out.println(queue.poll());
        System.out.println(queue.isEmpty());
    }
}
