package com.hochoy.jvm.classloader;

/**
 * Created by Hochoy on 2019/08/24.
 */
public class ClassLoadTime2 {

    public static void main(String[] args) {
       /* SuperClass [] superClasses = new SuperClass[10];
        System.out.println("newarray init ........");
        SuperClass superClass = superClasses[6];
        System.out.println("superClass is "+ superClass); // newarray 字节码不会触发初始化
        System.out.println("final variable:    " + SuperClass.CONST); //constant value 的调用不会触发初始化，
        // 在编译阶段通过常量传播优化，CONST 常量的值已经被存储到SuperClass类的常量池中，
        // 后续对SuperClass 的常量的引用实际上都被转化为对SuperClass类对自身常量池的引用
        System.out.println("==================================================================");
//        int a = SuperClass.value; // 静态变量被获取调用时，会被执行初始化，只会执行静态代码块，不会执行构造方法和非静态代码块

        System.out.println( SubClass.value);
        System.out.println("SubClass init.........................................");
        */
        new SubClass();// super-static --》 sub-static--》 super-not-static --》 super-constructor --》 sub-not-static--》sub-construct             super子类静态代码块-> 父类非静态代码块->父类构造方法-> 子类构造方法
//        new SuperClass();//  静态代码块-》非静态代码块-》构造方法
    }
    {
        System.out.println("Main no-static excued");
    }
    static {
        System.out.println("Main static executed");
    }

}
