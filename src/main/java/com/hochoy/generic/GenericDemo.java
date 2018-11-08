package com.hochoy.generic;

import org.apache.avro.generic.GenericData;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author :  hochoy
 * @version :  V1.0
 * @note :  TODO
 * @since :  Date : 2018年11月08日 14:28
 */
// https://segmentfault.com/a/1190000014824002
public class GenericDemo {}

/**
 * 泛型接口中 泛型类型 不能定义全局变量，只能使用在方法中
 * @param <T1>
 * @param <T2>
 */
interface Comparator<T1,T2>{

    /**
     * 接口中泛型类型的字母不能使用在全局变量中
     */
    //static final T1 MAX_VALUE = 100;

    static final int MAX_VALUE = 100; // ok
    void compare(T2 t);
    T2 compare();
    abstract T1 compare2(T2 t);
}

/**
 * 非泛型类中定义泛型方法
 */
class GenericMethod{
    /**
     * 泛型方法，在返回类型前面使用 泛型类型标识符
     * @param t
     * @param <T>
     */
    public static <T> void test1(T t){
        System.out.println(t);
    }

    /**
     * T 只能是List 或者List  的子类
     * @param t
     * @param <T>
     */
    public static <T extends List> void  test2(T t){
     t.add("addd");

    }

    /**
     * T...  可变参数
     * @param t
     * @param <T>
     */
    public static <T extends InputStream> void test3 (T...t){
        for(T temp :t){
            try {
                if (null != temp){
                    System.out.println(temp.toString());
                    temp.read();
                    temp.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        test1("java is a good language");
        ArrayList a = new ArrayList();
        a.add("2");
        test2(a);

        test3(new FileInputStream(System.getProperty("user.dir") + "/src/main/java/com/hochoy/generic/GenericTest.java"));
    }

}

/**
 * 泛型继承
 * 分为两种：
 * 1）保留父类泛型： 泛型子类
 * 2）不保留父类泛型： 子类按需实现
 *
 * 子类重写父类的方法，泛型类型随父类而定，
 * 子类使用父类的属性，该属性类型随父类定义的类型
 *
 * @param <T1>
 * @param <T2>
 */
abstract class Father<T1,T2>{

    T1 age;
    abstract void test(T2 name);
}

/**
 * 保留父类类型：-------------泛型子类
 * 1）全部保留
 * @param <T1>
 * @param <T2>
 */
class C1<T1,T2> extends Father<T1,T2>{
    @Override
    void test(T2 name) {

    }
}

/**
 * 2) 部分保留
 * @param <T1>
 */
class C2<T1> extends Father<T1,String>{
    @Override
    void test(String name) {

    }
}

/**
 * 不保留父类类型-------》子类按需实现
 * 1)具体类型
 */
class C3 extends Father<Integer,String>{
    @Override
    void test(String name) {

    }
}

/**
 * 2) 没有具体类型
 * 泛型擦除 ： 实现或继承父类 的子类，没有指定类型，类似于Object
 */
class C4 extends Father{
    @Override
    void test(Object name) {

    }
}

class Fruit{}
class Apple extends Fruit{}
class Pear extends Fruit{}
class RedApple extends Apple{}
class GeneTest<T extends Fruit>{
    static void test01(){
        GeneTest<Fruit> fruitGeneTest = new GeneTest<Fruit>();
        GeneTest<Apple> appleGeneTest = new GeneTest<Apple>();
        GeneTest<RedApple> redAppleGeneTest = new GeneTest<RedApple>();
    }
    static void test02(List<? extends Fruit> list){

    }
    static void test03(List<? super Apple> list){}

    public static void main(String[] args) {
        test02(new ArrayList<Fruit>());
        test02(new ArrayList<Apple>());
        test02(new ArrayList<RedApple>());
//        test02(new ArrayList<Object>());//Object 不是 Fruit 的子类 ，编译不通过

        test03( new ArrayList<Apple>());
//        test03(new ArrayList<RedApple>());// RedApple 不是 Apple的 父类 ，编译不能通过
        test03(new ArrayList<Fruit>());
    }
}




