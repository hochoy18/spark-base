package com.hochoy.design.pattern.singleton;

/**
 * Describe:   懒汉式单例模式 : 类加载时不初始化
 *  比较懒，在类加载时，不创建实例，因此类加载速度快，但运行时获取对象的速度慢
 *
 * @author hochoy <hochoy18@sina.com>
 * @version V1.0.0
 * @date 2019/4/22
 */
class LazySingleton {

    //静态私用成员，没有初始化
    //volatile :禁止指令重排序优化
    private volatile static LazySingleton instance = null;

    private LazySingleton() { //私有构造函数
        System.out.println("construct....");
    }

    /**
     * DCL :Double Check Lock
     * @return
     */
    public static LazySingleton getInstance() {
        if (null == instance){
            synchronized (LazySingleton.class){
                if (null == instance){
                    instance = new LazySingleton();
                }
            }
        }
        return instance;
    }

    /* public  static LazySingleton getInstance() {// 只有在getInstance被调用是才会被初始化加载
            //静态，同步，公开访问点
            if (instance == null){
                instance = new LazySingleton();
            }
            return instance;
        }*/
    public void out()
    {
        System.out.println("hashCode......."+instance.hashCode());
        System.out.println("Lazy singleton");
    }
}

