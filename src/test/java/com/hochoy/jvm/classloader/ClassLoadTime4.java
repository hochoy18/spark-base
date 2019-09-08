package com.hochoy.jvm.classloader;

public class ClassLoadTime4 {
    public static void main(String[] args)
    {
        new Son();  //测试入口
    }
}
class Grandpa
{
    static
    {
        System.out.println("爷爷静态代码");
    }

    {
        System.out.println("爷爷非静态代码块");
    }
    public Grandpa() {
        System.out.println("我是爷爷~");
    }
}
class Father extends Grandpa
{
    static
    {
        System.out.println("爸爸静态代码");
    }

    {
        System.out.println("爸爸在干啥");
    }

    public Father()
    {
        System.out.println("我是爸爸~");
    }
}
class Son extends Father
{
    static
    {
        System.out.println("儿子在静态代码块");
    }

    {
        System.out.println("儿子在干啥");
    }

    public Son()
    {
        System.out.println("我是儿子~");
    }
}
