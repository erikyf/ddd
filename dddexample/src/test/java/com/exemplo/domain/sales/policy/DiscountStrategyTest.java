package com.exemplo.domain.sales.policy;

import com.exemplo.domain.catalog.model.SKU;
import com.exemplo.domain.sales.model.OrderId;
import com.exemplo.domain.sales.model.SaleOrder;
import com.exemplo.domain.shared.DomainValidationException;
import com.exemplo.domain.shared.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DiscountStrategyTest {

    private SaleOrder order;

    @BeforeEach
    void setUp() {
        order = SaleOrder.create(OrderId.of("ORD-TEST"));
        order.addItem(SKU.of("SKU-1"), "Polo", Money.of(100.0), 2); // 200.00
    }

    @Test
    @DisplayName("NoDiscountStrategy should return zero discount")
    void testNoDiscount() {
        NoDiscountStrategy strategy = new NoDiscountStrategy();
        assertThat(strategy.calculateDiscount(order)).isEqualTo(Money.zero());
    }

    @Test
    @DisplayName("PercentageDiscountStrategy should calculate proportional discount")
    void testPercentageDiscount() {
        PercentageDiscountStrategy strategy = PercentageDiscountStrategy.of(15.0);
        Money discount = strategy.calculateDiscount(order);

        assertThat(discount).isEqualTo(Money.of(30.0)); // 15% of 200.00
    }

    @Test
    @DisplayName("PercentageDiscountStrategy should reject invalid percentage range")
    void testPercentageBounds() {
        assertThatThrownBy(() -> PercentageDiscountStrategy.of(-5.0))
                .isInstanceOf(DomainValidationException.class);

        assertThatThrownBy(() -> PercentageDiscountStrategy.of(105.0))
                .isInstanceOf(DomainValidationException.class);
    }

    @Test
    @DisplayName("VolumeDiscountStrategy should only trigger when item count meets threshold")
    void testVolumeDiscount() {
        // Requires 3 items for 20% discount
        VolumeDiscountStrategy strategy = VolumeDiscountStrategy.of(3, 20.0);

        // Currently 2 items in order -> discount should be zero
        assertThat(strategy.calculateDiscount(order)).isEqualTo(Money.zero());

        // Add 1 more item -> 3 total items -> triggers 20% discount
        order.addItem(SKU.of("SKU-2"), "Camiseta", Money.of(50.0), 1); // subtotal = 250.00
        Money discount = strategy.calculateDiscount(order);

        assertThat(discount).isEqualTo(Money.of(50.0)); // 20% of 250.00
    }
}
