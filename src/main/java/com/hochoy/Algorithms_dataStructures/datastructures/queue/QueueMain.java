package com.hochoy.Algorithms_dataStructures.datastructures.queue;

import java.util.Scanner;

public class QueueMain {


    public static void main(String[] args) {
        arrayQueueTest();
    }

    static void arrayQueueTest() {
        Queue queue = new ArrayQueue(4);

        Scanner scanner = new Scanner(System.in);

        boolean loop = true;

        while (loop) {
            String msg =
                    "\t-e(exit): \t退出\n" +
                            "\t-l{list): \tlist all element\n" +
                            "\t-h(head): \tshow front element of this queue\n" +
                            "\t-p(poll): \tpoll front element of this queue\n" +
                            "\t-a(add):  \tadd one element to the rear of the queue\n" +
                            "\t--help:  \tshow this message ";

            String b = scanner.next();

            switch (b) {
                case "-e":
                    loop = false;
                    break;
                case "-l":
                    try {
                        queue.list();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case "-h":
                    try {
                        int head = queue.head();
                        System.out.printf("head element is %d%n",head);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case "-p":
                    try {
                        int poll = queue.poll();
                        System.out.printf("the element polled is %d%n",poll);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case "-a":
                    try {
                        System.out.println("input an element to this queue ");
                        int i = scanner.nextInt();
                        queue.add(i);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case "--help":
                    System.out.println(msg);
                    break;
                default:
                    System.out.println(msg);
                    break;


            }
        }
    }
}


class CircleQueue implements Queue{
    @Override
    public boolean isFull() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void add(int e) {

    }

    @Override
    public int poll() {
        return 0;
    }

    @Override
    public int head() {
        return 0;
    }

    @Override
    public void list() {

    }
}

class ArrayQueue implements Queue {
    private int maxSize;
    private int front;
    private int rear;
    private int[] value;

    public ArrayQueue(int maxSize) {
        this.maxSize = maxSize;
        value = new int[maxSize];
        front = -1;
        rear = -1;
    }


    @Override
    public boolean isFull() {
        return rear == maxSize - 1;
    }

    @Override
    public boolean isEmpty() {
        return rear == front;
    }

    @Override
    public void add(int e) {
        if (isFull()) {
            throw new RuntimeException("The queue is full ...");
        }
        value[++rear] = e;
    }

    @Override
    public int poll() {
        if (isEmpty()) {
            throw new RuntimeException("The queue is empty ...");
        }
        ++ front;
        int res = value[front];
        value[front] = 0;
        return res;
    }

    @Override
    public int head() {
        if (isEmpty()) {
            throw new RuntimeException("The queue is empty ...");
        }
        return value[front + 1];
    }

    @Override
    public void list() {
        if (isEmpty())
            throw new RuntimeException("The queue is empty ...");
        for (int i = front + 1; i <= rear; i++) {
            System.out.println(value[i]);
        }
    }
}

interface Queue {

    boolean isFull();

    boolean isEmpty();

    void add(int e);

    int poll();

    int head();

    void list();

}