package com.hochoy.Algorithms_dataStructures.stack;


import com.hochoy.Algorithms_dataStructures.datastructures.stack.Stack;
import org.junit.Assert;
import org.junit.Test;

public class StackTest {

    @Test
    public void testStack(){
        Stack<Integer> stack = new Stack<>();
        stack.push(1);
        System.out.println("size : "+stack.size());
        Assert.assertEquals(1,stack.size());
        stack.push(2);
        System.out.println("size : "+stack.size());
        Assert.assertEquals(2,stack.size());

        stack.push(3);
        System.out.println("size : "+stack.size());
        Assert.assertEquals(3,stack.size());

        stack.push(4);
        System.out.println("size : "+stack.size());
        Assert.assertEquals(4,stack.size());

        Integer pop = stack.pop();
        System.out.println("element of pop is : "+pop);
        System.out.println("size : "+stack.size());
        Assert.assertEquals(3,stack.size());


        pop = stack.pop();
        System.out.println("element of pop is : "+pop);
        System.out.println("size : "+stack.size());
        Assert.assertEquals(2,stack.size());

        pop = stack.pop();
        System.out.println("element of pop is : "+pop);
        System.out.println("size : "+stack.size());
        Assert.assertEquals(1,stack.size());

        pop = stack.pop();
        System.out.println("element of pop is : "+pop);
        System.out.println("size : "+stack.size());
        Assert.assertEquals(0,stack.size());


    }
}
