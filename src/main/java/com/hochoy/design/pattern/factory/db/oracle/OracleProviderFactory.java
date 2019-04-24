package com.hochoy.design.pattern.factory.db.oracle;

import com.hochoy.design.pattern.factory.db.DBOperation;
import com.hochoy.design.pattern.factory.db.Provider;

/**
 * Describe:
 *
 * @author hochoy <hochoy18@sina.com>
 * @version V1.0.0
 * @date 2019/4/24
 */
public class OracleProviderFactory implements Provider {
    @Override
    public DBOperation produce() {
        return OracleOperation.getInstance();
    }

}
