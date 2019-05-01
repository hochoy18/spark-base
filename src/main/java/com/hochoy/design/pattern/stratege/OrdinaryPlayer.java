package com.hochoy.design.pattern.stratege;

/**
 * Describe:
 *
 * @author hochoy <hochoy18@sina.com>
 * @version V1.0.0
 * @date 2019/4/26
 */
@PriceRegion(max = 10000)
public class OrdinaryPlayer implements ICallPriceStratege{

    @Override
    public double callPrice(double price) {
        return price;
    }
}
