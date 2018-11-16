package com.hochoy.test;

/**
 * @author :  hochoy
 * @version :  V1.0
 * @note :  TODO
 * @since :  Date : 2018年11月16日 14:39
 */

public class JavaSAMTest {
    public static void main(String[] args) {
        JavaSAMTest test = new JavaSAMTest();
//        test.oneMethodTest(i -> i);
    }

    public void oneMethodTest(OneMethodInterface oneMethodInterface) {
        oneMethodInterface.toString();
    }

    @FunctionalInterface
    interface OneMethodInterface {
        int test3(int i);
    }
}
