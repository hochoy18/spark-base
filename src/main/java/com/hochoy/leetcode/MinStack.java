package com.hochoy.leetcode;

import java.util.Comparator;
import java.util.Stack;
import java.util.function.Function;

public class MinStack  {

    private Stack<Integer> stack;// = new Stack();
    public MinStack() {
        stack = new Stack();
    }

    public void push(int x) {
        stack.push(x);
    }

    public void pop() {
        stack.pop();

    }

    public int top() {
        return  stack.peek();
    }

    public int getMin() {
        return stack.stream().min(Comparator.comparing(Function.identity())).orElse(0);
    }

    public static void main(String[] args) {
        MinStack minStack = new MinStack();
        minStack.push(-2);
        minStack.push(0);
        minStack.push(-3);
        int min = minStack.getMin();// --> 返回 -3.
        System.out.println(min);
        minStack.pop();
        int  top = minStack.top();      //--> 返回 0.
        System.out.println(top);
          min = minStack.getMin();   // --> 返回 -2.
        System.out.println(min);

    }
}
