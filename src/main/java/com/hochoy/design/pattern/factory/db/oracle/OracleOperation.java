package com.hochoy.design.pattern.factory.db.oracle;

import com.hochoy.design.pattern.factory.db.DBOperation;

/**
 * Describe:
 *
 * @author hochoy <hochoy18@sina.com>
 * @version V1.0.0
 * @date 2019/4/24
 */
public class OracleOperation implements DBOperation {
    @Override
    public void insert() {
        System.out.println("oracle insert into ..........");
    }
    private static volatile OracleOperation operation = null;
    private OracleOperation() {
    }

    public static OracleOperation getInstance() {
        if (null == operation){
            synchronized (OracleOperation.class){
                if (null == operation){
                    operation = new OracleOperation();
                }
            }
        }
        return operation;
    }

}
