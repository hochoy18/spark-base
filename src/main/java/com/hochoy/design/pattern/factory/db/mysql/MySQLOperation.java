package com.hochoy.design.pattern.factory.db.mysql;

import com.hochoy.design.pattern.factory.db.DBOperation;

/**
 * Describe:
 *
 * @author hochoy <hochoy18@sina.com>
 * @version V1.0.0
 * @date 2019/4/24
 */
public class MySQLOperation implements DBOperation {
    @Override
    public void insert() {
        String insert = "insert into test (id,name) values (1,1)";
        System.out.println("MySQL.........."+insert);
    }

    private volatile static MySQLOperation instance = null;

    private MySQLOperation() {}

    public static MySQLOperation getInstance() {
        if (null == instance){
            synchronized (MySQLOperation.class){
                if (null == instance){
                    instance = new MySQLOperation();
                }
            }
        }
        return instance;
    }
}
