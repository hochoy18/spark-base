package com.hochoy.design.pattern.singleton;

/**
 * Describe:
 *
 * @author hochoy <hochoy18@sina.com>
 * @version V1.0.0
 * @date 2019/4/22
 */
public class SingletonMain {
    public static void main(String[] args) {

        new Thread(new Runnable() {
            @Override
            public void run() {
               new T().t();
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                LazySingleton lazySingleton = LazySingleton.getInstance();
                System.out.println(lazySingleton);
                lazySingleton.out();
            }
        }).start();


        EagerSingleton eagerSingleton = EagerSingleton.getInstance();
        System.out.println(eagerSingleton.hashCode());
    }
}
class T {
    void t(){
        LazySingleton lazySingleton;
        lazySingleton    = LazySingleton.getInstance();
        System.out.println(lazySingleton);
        lazySingleton.out();
    }
}
