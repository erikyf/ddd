package com.exemplo.domain.sales.model;

import com.exemplo.domain.catalog.model.SKU;
import com.exemplo.domain.customer.model.CPF;
import com.exemplo.domain.sales.policy.PercentageDiscountStrategy;
import com.exemplo.domain.shared.DomainValidationException;
import com.exemplo.domain.shared.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SaleOrderTest {

    private SaleOrder order;
    private SKU shirtSku;
    private SKU pantsSku;

    @BeforeEach
    void setUp() {
        order = SaleOrder.create(OrderId.of("ORD-001"), CPF.of("12345678909"));
        shirtSku = SKU.of("SHIRT-BLU-M");
        pantsSku = SKU.of("PANTS-BLK-40");
    }

    @Test
    @DisplayName("Should create sale order in DRAFT status")
    void shouldCreateOrderInDraft() {
        assertThat(order.getId().getValue()).isEqualTo("ORD-001");
        assertThat(order.getCustomerCpf()).isPresent();
        assertThat(order.getCustomerCpf().get().getValue()).isEqualTo("12345678909");
        assertThat(order.getStatus()).isEqualTo(OrderStatus.DRAFT);
        assertThat(order.getItems()).isEmpty();
        assertThat(order.calculateSubtotal()).isEqualTo(Money.zero());
        assertThat(order.calculateTotal()).isEqualTo(Money.zero());
    }

    @Test
    @DisplayName("Should add items and accumulate quantities")
    void shouldAddItems() {
        order.addItem(shirtSku, "Camisa Linho", Money.of(150.0), 2);
        order.addItem(shirtSku, "Camisa Linho", Money.of(150.0), 1); // Increment quantity
        order.addItem(pantsSku, "Calça Sarja", Money.of(200.0), 1);

        assertThat(order.getItems()).hasSize(2);
        assertThat(order.getTotalItemCount()).isEqualTo(4);
        assertThat(order.calculateSubtotal()).isEqualTo(Money.of(650.0)); // 3*150 + 1*200 = 650
    }

    @Test
    @DisplayName("Should remove items while in DRAFT status")
    void shouldRemoveItems() {
        order.addItem(shirtSku, "Camisa Linho", Money.of(150.0), 1);
        order.removeItem(shirtSku);

        assertThat(order.getItems()).isEmpty();
        assertThat(order.calculateSubtotal()).isEqualTo(Money.zero());
    }

    @Test
    @DisplayName("Should calculate percentage discount correctly")
    void shouldCalculateDiscount() {
        order.addItem(shirtSku, "Camisa", Money.of(200.0), 1);
        order.applyDiscountStrategy(PercentageDiscountStrategy.of(10)); // 10%

        assertThat(order.calculateSubtotal()).isEqualTo(Money.of(200.0));
        assertThat(order.calculateDiscount()).isEqualTo(Money.of(20.0));
        assertThat(order.calculateTotal()).isEqualTo(Money.of(180.0));
    }

    @Test
    @DisplayName("Should confirm order and transition to CONFIRMED")
    void shouldConfirmOrder() {
        order.addItem(shirtSku, "Camisa", Money.of(100.0), 1);
        order.confirm();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
    }

    @Test
    @DisplayName("Should reject confirmation if order has no items")
    void shouldRejectEmptyConfirmation() {
        assertThatThrownBy(() -> order.confirm())
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("zero items");
    }

    @Test
    @DisplayName("Should reject modifications when order is not in DRAFT")
    void shouldRejectModificationsAfterConfirmation() {
        order.addItem(shirtSku, "Camisa", Money.of(100.0), 1);
        order.confirm();

        assertThatThrownBy(() -> order.addItem(pantsSku, "Calça", Money.of(50.0), 1))
                .isInstanceOf(DomainValidationException.class);

        assertThatThrownBy(() -> order.removeItem(shirtSku))
                .isInstanceOf(DomainValidationException.class);
    }

    @Test
    @DisplayName("Should pay confirmed order successfully")
    void shouldPayOrder() {
        order.addItem(shirtSku, "Camisa", Money.of(120.0), 1);
        order.confirm();

        order.pay(PaymentMethod.PIX, Money.of(120.0));

        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
        assertThat(order.getPaymentRecord()).isPresent();
        assertThat(order.getPaymentRecord().get().getMethod()).isEqualTo(PaymentMethod.PIX);
        assertThat(order.getPaymentRecord().get().getAmount()).isEqualTo(Money.of(120.0));
    }

    @Test
    @DisplayName("Should reject payment with insufficient amount")
    void shouldRejectUnderpayment() {
        order.addItem(shirtSku, "Camisa", Money.of(100.0), 1);
        order.confirm();

        assertThatThrownBy(() -> order.pay(PaymentMethod.CASH, Money.of(90.0)))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("Insufficient payment amount");
    }

    @Test
    @DisplayName("Should cancel order")
    void shouldCancelOrder() {
        order.cancel();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);

        assertThatThrownBy(() -> order.cancel())
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("already cancelled");
    }
}
