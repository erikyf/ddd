package com.exemplo.domain.sales.policy;

import com.exemplo.domain.sales.model.SaleOrder;
import com.exemplo.domain.shared.Money;

/**
 * Strategy pattern interface for applying promotional and custom discounts to a SaleOrder.
 */
@FunctionalInterface
public interface DiscountStrategy {

    Money calculateDiscount(SaleOrder order);

    default String getName() {
        return getClass().getSimpleName();
    }
}
