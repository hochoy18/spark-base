package com.hochoy.Algorithms_dataStructures.stack;


import com.hochoy.Algorithms_dataStructures.datastructures.stack.Stack;
import com.hochoy.Algorithms_dataStructures.datastructures.stack.StackDemo;
import org.junit.Test;

import static org.junit.Assert.*;

public class StackTest {

    @Test
    public void testStack(){
        Stack<Integer> stack = new Stack<>();
        stack.push(1);
        System.out.println("size : "+stack.size());
        assertEquals(1,stack.size());
        stack.push(2);
        System.out.println("size : "+stack.size());
        assertEquals(2,stack.size());

        stack.push(3);
        System.out.println("size : "+stack.size());
        assertEquals(3,stack.size());

        stack.push(4);
        System.out.println("size : "+stack.size());
        assertEquals(4,stack.size());

        System.out.println("========================");
        for (Object o : stack) {
            System.out.println(o);
        }


        Integer pop = stack.pop();
        System.out.println("element of pop is : "+pop);
        System.out.println("size : "+stack.size());
        assertEquals(3,stack.size());




        pop = stack.pop();
        System.out.println("element of pop is : "+pop);
        System.out.println("size : "+stack.size());
        assertEquals(2,stack.size());

        pop = stack.pop();
        System.out.println("element of pop is : "+pop);
        System.out.println("size : "+stack.size());
        assertEquals(1,stack.size());

        pop = stack.pop();
        System.out.println("element of pop is : "+pop);
        System.out.println("size : "+stack.size());
        assertEquals(0,stack.size());


    }

    @Test
    public void bracketsIsMatch (){
        String str;
        boolean res;

        str = "(shanghai)(长安)";
        res = StackDemo.bracketsIsMatch(str);
        assertTrue(res);

        str = "shanghai((长安))";
        res = StackDemo.bracketsIsMatch(str);
        assertTrue(res);

        str = "shanghai(长安(北京)(深圳)南京)";
        res = StackDemo.bracketsIsMatch(str);
        assertTrue(res);

        str = "shanghai(长安))";
        res = StackDemo.bracketsIsMatch(str);
        assertFalse(res);

        str = "((shanghai)(长安)";
        res = StackDemo.bracketsIsMatch(str);
        assertFalse(res);





    }

    @Test
    public void conversion(){
        int conversion = StackDemo.conversion(3467,8);
        System.out.println(conversion);
        assertEquals(6613,conversion);

        conversion = StackDemo.conversion(10,2);
        System.out.println(conversion);
        assertEquals(1010,conversion);

        conversion = StackDemo.conversion(15,2);
        System.out.println(conversion);
        assertEquals(1111,conversion);
    }
}
