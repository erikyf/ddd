package com.exemplo.application.sales;

import com.exemplo.application.dto.OrderDTO;
import com.exemplo.application.dto.ProductDTO;
import com.exemplo.application.dto.VariantDTO;
import com.exemplo.application.catalog.CatalogService;
import com.exemplo.domain.catalog.model.ClothingCategory;
import com.exemplo.domain.catalog.model.ClothingSize;
import com.exemplo.domain.catalog.model.Color;
import com.exemplo.domain.sales.model.PaymentMethod;
import com.exemplo.domain.sales.policy.PercentageDiscountStrategy;
import com.exemplo.domain.shared.DomainValidationException;
import com.exemplo.domain.shared.Money;
import com.exemplo.infrastructure.persistence.InMemoryProductRepository;
import com.exemplo.infrastructure.persistence.InMemorySaleOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SalesServiceTest {

    private SalesService salesService;
    private CatalogService catalogService;
    private InMemoryProductRepository productRepository;
    private InMemorySaleOrderRepository orderRepository;

    private VariantDTO shirtVariant;

    @BeforeEach
    void setUp() {
        productRepository = new InMemoryProductRepository();
        orderRepository = new InMemorySaleOrderRepository();

        catalogService = new CatalogService(productRepository);
        salesService = new SalesService(orderRepository, productRepository);

        ProductDTO product = catalogService.registerProduct("Camisa Polo", "Lacoste", ClothingCategory.SHIRTS, Money.of(200.0));
        shirtVariant = catalogService.addVariant(product.id(), ClothingSize.M, Color.BLUE, "7891122334455", 10, 2);
    }

    @Test
    @DisplayName("Should execute complete sales lifecycle: create, add items, apply discount, checkout, deduct stock")
    void shouldExecuteCompleteSalesLifecycle() {
        // 1. Create order
        OrderDTO order = salesService.createOrder("12345678909");
        assertThat(order.orderId()).isNotBlank();
        assertThat(order.status()).isEqualTo("Em Aberto");

        // 2. Add item (2 units)
        order = salesService.addItem(order.orderId(), shirtVariant.sku(), 2);
        assertThat(order.totalItems()).isEqualTo(2);
        assertThat(order.subtotal()).isEqualByComparingTo("400.00");

        // 3. Apply 10% discount
        order = salesService.applyDiscount(order.orderId(), PercentageDiscountStrategy.of(10));
        assertThat(order.discount()).isEqualByComparingTo("40.00");
        assertThat(order.total()).isEqualByComparingTo("360.00");

        // 4. Checkout with PIX
        order = salesService.checkout(order.orderId(), PaymentMethod.PIX, Money.of(360.0));
        assertThat(order.status()).isEqualTo("Pago");
        assertThat(order.paymentMethod()).isEqualTo("Pix");

        // 5. Verify stock was deducted from catalog (10 - 2 = 8)
        ProductDTO updatedProduct = catalogService.findBySku(shirtVariant.sku()).orElseThrow();
        assertThat(updatedProduct.totalStock()).isEqualTo(8);

        // 6. Verify revenue
        assertThat(salesService.calculateTotalRevenue()).isEqualTo(Money.of(360.0));
    }

    @Test
    @DisplayName("Should prevent adding item if requested quantity exceeds available stock")
    void shouldPreventExceedingStock() {
        OrderDTO order = salesService.createOrder(null);

        assertThatThrownBy(() -> salesService.addItem(order.orderId(), shirtVariant.sku(), 15))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("Insufficient stock");
    }

    @Test
    @DisplayName("Should cancel order")
    void shouldCancelOrder() {
        OrderDTO order = salesService.createOrder(null);
        OrderDTO cancelled = salesService.cancelOrder(order.orderId());

        assertThat(cancelled.status()).isEqualTo("Cancelado");
    }
}
