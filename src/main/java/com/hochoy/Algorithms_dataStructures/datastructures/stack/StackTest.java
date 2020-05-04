package com.hochoy.Algorithms_dataStructures.datastructures.stack;


import java.util.Scanner;

public class StackTest {

    public static void main(String[] args) {
        stackTest();
    }

    static void stackTest() {
        Stack stack = new Stack(5);
        Scanner scanner = new Scanner(System.in);
        String msg =
                "\t-exit: \t退出\n" +
                        "\t--list: \tlist all element\n" +
                        "\t--peek: \tget top element of this stack\n" +
                        "\t--pop: \tpoll the top element of this stack\n" +
                        "\t--push:  \tadd one element to the top of the stack\n" +
                        "\t--help:  \tshow this message ";
        boolean loop = true;
        while (loop) {
            String b = scanner.next();
            try {
                switch (b) {
                    case "--list":
                        stack.list();
                        break;
                    case "--peek":
                        int peek = stack.peek();
                        System.out.printf("top element of this stack %d%n", peek);
                        break;
                    case "--pop":
                        int head = stack.pop();
                        System.out.printf("poll the top element of this stack %d%n", head);
                        break;
                    case "--push":
                        System.out.println("input an element to this stack  ");
                        int i = scanner.nextInt();
                        stack.push(i);
                        break;
                    case "--help":
                        System.out.println(msg);
                        break;
                    case "--exit":
                        loop = false;
                        break;
                    default:
                        System.out.println(msg);
                        break;
                }
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
            }
        }

    }

}


class Stack {

    private int maxSize;
    private int top = -1;
    private int[] stack;

    public Stack(int maxSize) {
        this.maxSize = maxSize;
        stack = new int[maxSize];
    }

    public boolean isFull() {
        return top == maxSize - 1;
    }

    public boolean isEmpty() {
        return top == -1;
    }

    public void push(int data) {
        if (isFull())
            throw new RuntimeException("the stack is full....");
        stack[++top] = data;
    }

    public void list() {
        if (isEmpty())
            throw new RuntimeException("the stack  is empty ...");
        int tmp = top;
        for (int i = tmp; i >= 0; i--) {
            System.out.printf("stack[%d]:%d", i, stack[top--]);
        }
    }

    public int pop() {
        if (isEmpty()) {
            throw new RuntimeException("the stack is empty ...");
        }
        stack[top] = 0;
        return stack[top--];
    }

    public int peek() {
        if (isEmpty())
            throw new RuntimeException("the stack is empty ...");
        return stack[top];
    }
}