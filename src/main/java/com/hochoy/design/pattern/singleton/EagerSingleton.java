package com.hochoy.design.pattern.singleton;

/**
 * Describe:在类加载时就完成了初始化，所以类加载较慢，但获取对象的速度快,对对象的实例化比懒汉式要来不及
 *
 * @author hochoy <hochoy18@sina.com>
 * @version V1.0.0
 * @date 2019/4/22
 */
public class EagerSingleton {
    //饿汉单例模式

    //在类加载时就完成了初始化，所以类加载较慢，但获取对象的速度快

    //静态私有成员，已初始化，在类加载是就完成了初始化
    private static EagerSingleton instance = new EagerSingleton();

    private EagerSingleton() { //私有构造函数
        System.out.println("私有构造函数.....");
    }

    public static EagerSingleton getInstance() {
        return instance;
    }
}
