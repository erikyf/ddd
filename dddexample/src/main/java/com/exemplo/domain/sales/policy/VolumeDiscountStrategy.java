package com.exemplo.domain.sales.policy;

import com.exemplo.domain.sales.model.SaleOrder;
import com.exemplo.domain.shared.DomainValidationException;
import com.exemplo.domain.shared.Money;

import java.math.BigDecimal;

/**
 * Strategy that grants a discount if the customer buys a minimum quantity of items.
 * Example: "Buy 3 or more clothing items and get 15% off".
 */
public class VolumeDiscountStrategy implements DiscountStrategy {

    private final int minQuantity;
    private final BigDecimal discountPercentage;

    public VolumeDiscountStrategy(int minQuantity, BigDecimal discountPercentage) {
        if (minQuantity <= 1) {
            throw new DomainValidationException("Minimum quantity for volume discount must be > 1, but was: " + minQuantity);
        }
        if (discountPercentage == null || discountPercentage.compareTo(BigDecimal.ZERO) <= 0 || discountPercentage.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new DomainValidationException("Discount percentage must be between 0 and 100, but was: " + discountPercentage);
        }
        this.minQuantity = minQuantity;
        this.discountPercentage = discountPercentage;
    }

    public static VolumeDiscountStrategy of(int minQuantity, double discountPercentage) {
        return new VolumeDiscountStrategy(minQuantity, BigDecimal.valueOf(discountPercentage));
    }

    @Override
    public Money calculateDiscount(SaleOrder order) {
        if (order == null) return Money.zero();
        if (order.getTotalItemCount() >= minQuantity) {
            return order.calculateSubtotal().percentage(discountPercentage);
        }
        return Money.zero();
    }

    @Override
    public String getName() {
        return String.format("Desconto Progressivo: %s%% comprando %d ou mais itens", discountPercentage, minQuantity);
    }
}
