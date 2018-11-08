package com.hochoy.generic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author :  hochoy
 * @version :  V1.0
 * @note :  TODO
 * @since :  Date : 2018年11月08日 9:45
 */

public class GenericTest {
    public static void main(String[] args) {
        /**
         * 实例化泛型类时，泛型类型不能是基本数据类型，
         */
        //Box<int> i = new Box<int>(123);

        Box<Integer> a = new Box<Integer>(222);
        Box<Number> b = new Box<Number>(321);
        Box<Float> f = new Box<Float>(3.1415f);
        System.out.println(a.getData() + "   " + b.getData() + "  " + f.getData());
        System.out.println(a.getClass() + "----" + b.getClass() + "----" + (a.getClass() == b.getClass()));

        Box<String> s = new Box<String>("hello world ");
        getData(b);
        getData(a);
        getData(f);
        getData(s);
        getUpperNumberData(b);
        getUpperNumberData(a);
        getUpperNumberData(f);
//        getUpperNumberData(s);//String 不是 Number的子类
        getSuperData(b);
    }

    public static void getData(Box<?> box) {
        System.out.println("data  :  " + box.getData());
    }

    public static void getUpperNumberData(Box<? extends Number> data) {
        System.out.println("upperNumberData :  " + data.getData());
    }

    public static void getSuperData(Box<? super Number> data) {
        System.out.println("superData :  " + data.getData());
    }

}

class Box<T> {
    private T data;

    /**
     * 泛型类的 泛型类型不能使用在静态属性上
     */
//    private static T javaee;

    public Box() {
    }

    public Box(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

class ListTest {
    public static void main(String[] args) {
        List list = new ArrayList();
        list.add("abcdefg");
        list.add(100);
        for (int i = 0; i < list.size(); i++) {
            String s = (String) list.get(i);
            System.out.println("item = " + s);
        }
    }
}


class GenericClassMain {
    public static void main(String[] args) {
        GenericClass genericInteger = new GenericClass<Integer>(123);
        GenericClass genericString = new GenericClass<String>("hello world ");
        GenericClass generic = new GenericClass(222.12f);
        GenericClass genericBool = new GenericClass(false);
        System.out.println(
                genericInteger.getKey()
                        + "   " + genericString.getKey()
                        + "  " + generic.getKey()
                        + "   " + genericBool.getKey()
        );
        /**
         * 不能对确切的泛型类型使用instanceof操作。如下面的操作是非法的，编译时会出错
         * System.out.println("instanceof  "+ (genericBool instanceof  GenericClass));
         *
         * 而  instanceof 的泛型类中没有加 <泛型类类型> 则 该 instanceof 永远为 true，
         * 以下判断永远为true
         */
        System.out.println("instanceof  " + (genericBool instanceof GenericClass));
    }
}

/**
 * 此处 mmm 可以随便写为任意标识符，常见的如T、E、K、V 等形式的参数都可以表示泛型
 * 在实例化泛型类时，必须指定 mmm 的具体类型
 * 泛型的类型参数只能是类类型，不能是简单类型
 */
class GenericClass<mmm> {

    /**
     * key 这个成员变量的类型为 mmm ，mmm 的类型由外部定义
     */
    private mmm key;


    public GenericClass(mmm key) {
        /**
         * 泛型构造方法形参 key 的类型也为 mmm ，mmm的类型由外部定义
         */
        this.key = key;
    }

    /**
     * 泛型类的getter、  setter 方法的返回值、形参类型也是 mmm
     * mmm 的类型由外部定义
     *
     * @return
     */
    public mmm getKey() {
        return key;
    }

    public void setKey(mmm key) {
        this.key = key;
    }
}

class FruitGenerator implements Generator<String> {
    private String[] fruits = new String[]{
            "apple", "Banana", "pear","aaaa","bbbbb","cccc"
    };

    public String next() {
        Random random = new Random();
        return fruits[random.nextInt(6)];
    }

    public static void main(String[] args) {
        System.out.println(new FruitGenerator().next());
    }
}

/**
 * 泛型接口与泛型类的定义及使用基本相同。泛型接口常被用在各种类的生产器中，可以看一个例子
 *
 * @param <QQQ>
 */
interface Generator<QQQ> {
    QQQ next();
}


