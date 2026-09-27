package com.exemplo.domain.sales.policy;

import com.exemplo.domain.sales.model.SaleOrder;
import com.exemplo.domain.shared.DomainValidationException;
import com.exemplo.domain.shared.Money;

import java.math.BigDecimal;

/**
 * Strategy that applies a percentage discount (e.g. 10%, 20%) over the order subtotal.
 */
public class PercentageDiscountStrategy implements DiscountStrategy {

    private final BigDecimal percentage;

    public PercentageDiscountStrategy(BigDecimal percentage) {
        if (percentage == null || percentage.compareTo(BigDecimal.ZERO) < 0 || percentage.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new DomainValidationException("Percentage must be between 0 and 100, but was: " + percentage);
        }
        this.percentage = percentage;
    }

    public static PercentageDiscountStrategy of(double percentage) {
        return new PercentageDiscountStrategy(BigDecimal.valueOf(percentage));
    }

    @Override
    public Money calculateDiscount(SaleOrder order) {
        if (order == null) return Money.zero();
        return order.calculateSubtotal().percentage(percentage);
    }

    @Override
    public String getName() {
        return percentage + "% de Desconto Promocional";
    }
}
