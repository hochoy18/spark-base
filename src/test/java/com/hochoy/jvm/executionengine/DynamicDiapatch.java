package com.hochoy.jvm.executionengine;

import java.util.concurrent.CountDownLatch;

/**
 * Describe:
 *
 * @author hochoy <hochoy18@sina.com>
 * @version V1.0.0
 * @date 2019/3/28
 */
  public    class DynamicDiapatch  {

    static abstract class Human{
        protected abstract void sayHello();
    }
    static class Man extends Human{
        @Override
        protected void sayHello() {
            System.out.println("Man  say  hello ");
        }
    }
    static class Woman extends Human{
        @Override
        protected void sayHello() {
            System.out.println("Woman says hello");
        }
    }

    public static void main(String[] args) {
//        java.lang.invoke.MethodType
//        CountDownLatch
        Human man = new Man();
        Human woman = new Woman();
        man.sayHello();
        woman.sayHello();
        man = new Woman();
        man.sayHello();
    }
}
