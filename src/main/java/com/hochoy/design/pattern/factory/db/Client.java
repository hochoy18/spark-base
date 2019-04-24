package com.hochoy.design.pattern.factory.db;

/**
 * Describe:
 *
 * @author hochoy <hochoy18@sina.com>
 * @version V1.0.0
 * @date 2019/4/24
 */
public class Client {
    public static void main(String[] args) {
        String clazz = "com.hochoy.design.pattern.factory.db.oracle.OracleProviderFactory";
        try {
            Provider factory;
            try {
                factory = (Provider)Class.forName(clazz).newInstance();
                DBOperation operation = factory.produce();
                operation.insert();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
