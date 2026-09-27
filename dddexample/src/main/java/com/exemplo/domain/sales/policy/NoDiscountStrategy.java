package com.exemplo.domain.sales.policy;

import com.exemplo.domain.sales.model.SaleOrder;
import com.exemplo.domain.shared.Money;

/**
 * Default policy: no discount applied.
 */
public class NoDiscountStrategy implements DiscountStrategy {

    @Override
    public Money calculateDiscount(SaleOrder order) {
        return Money.zero();
    }

    @Override
    public String getName() {
        return "Sem Desconto";
    }
}
