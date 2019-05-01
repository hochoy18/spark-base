package com.hochoy.design.pattern.stratege;

/**
 * Describe:
 *
 * @author hochoy <hochoy18@sina.com>
 * @version V1.0.0
 * @date 2019/4/26
 */
@PriceRegion(min = 10000, max = 20000,type = DBType.MYSQL)
public class Member implements ICallPriceStratege{

    @Override
    public double callPrice(double price) {
        return price * 0.9;
    }
}
