package com.hochoy.jvm.classloader;

/**
 * Created by Hochoy on 2019/08/24.
 * 触发初始化 1：  静态变量的获取和设置、静态方法的调用、new 对象
 *
 *
 */
public class ClassLoadTime1 {
    public static void main(String[] args) {
        System.out.println(SubClass.value);
    }
}

class SuperClass {
    static {
        System.out.println(">>>>>>>>>>>>>>>> Super Class static");
    }

    {
        System.out.println(">>>>>>>>>>>>    Super Class not static ");
    }
    public final static String CONST = "CONSTANT";
    public static int value = 1000;

    public SuperClass() {
        System.out.println("Constructor>>>>>>>>>>>>>> SuperClass....................");
    }
}

class SubClass extends SuperClass {
    static {
        System.out.println("<<<<<<<<<<<    Sub Class static !!!");
    }
    {
        System.out.println(">>>>>>>>>>>>    Sub Class not static ");
    }
    public SubClass() {
        System.out.println("Constructor>>>>>>>>>>>>>> SubClass....................");
    }
}



