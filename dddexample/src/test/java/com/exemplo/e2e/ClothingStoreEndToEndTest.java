package com.exemplo.e2e;

import com.exemplo.application.catalog.CatalogService;
import com.exemplo.application.dto.OrderDTO;
import com.exemplo.application.dto.ProductDTO;
import com.exemplo.application.dto.VariantDTO;
import com.exemplo.application.sales.SalesService;
import com.exemplo.domain.catalog.event.StockLowEvent;
import com.exemplo.domain.catalog.model.ClothingCategory;
import com.exemplo.domain.catalog.model.ClothingSize;
import com.exemplo.domain.catalog.model.Color;
import com.exemplo.domain.sales.event.OrderPaidEvent;
import com.exemplo.domain.sales.model.PaymentMethod;
import com.exemplo.domain.sales.policy.PercentageDiscountStrategy;
import com.exemplo.domain.sales.policy.VolumeDiscountStrategy;
import com.exemplo.domain.shared.Money;
import com.exemplo.domain.shared.event.DomainEventPublisher;
import com.exemplo.infrastructure.persistence.InMemoryProductRepository;
import com.exemplo.infrastructure.persistence.InMemorySaleOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ClothingStoreEndToEndTest {

    private CatalogService catalog;
    private SalesService sales;
    private List<OrderPaidEvent> paidEvents;
    private List<StockLowEvent> lowStockEvents;

    @BeforeEach
    void setUp() {
        InMemoryProductRepository productRepo = new InMemoryProductRepository();
        InMemorySaleOrderRepository orderRepo = new InMemorySaleOrderRepository();

        catalog = new CatalogService(productRepo);
        sales = new SalesService(orderRepo, productRepo);

        paidEvents = new ArrayList<>();
        lowStockEvents = new ArrayList<>();
        DomainEventPublisher.clearSubscribers();
        DomainEventPublisher.subscribe(OrderPaidEvent.class, paidEvents::add);
        DomainEventPublisher.subscribe(StockLowEvent.class, lowStockEvents::add);
    }

    @Test
    @DisplayName("End-to-End Simulation: Store Catalog Setup -> Customer Orders -> Discounts -> Stock Deductions -> Revenue Audit")
    void executeFullStoreWorkflow() {
        // --- 1. Set Up Store Catalog ---
        ProductDTO polo = catalog.registerProduct("Polo Piquet", "Dudalina", ClothingCategory.SHIRTS, Money.of(150.0));
        VariantDTO poloM = catalog.addVariant(polo.id(), ClothingSize.M, Color.NAVY, "7891000000010", 5, 2);
        VariantDTO poloG = catalog.addVariant(polo.id(), ClothingSize.G, Color.WHITE, "7891000000020", 3, 2);

        ProductDTO dress = catalog.registerProduct("Vestido Seda", "Farm", ClothingCategory.DRESSES, Money.of(400.0));
        VariantDTO dressP = catalog.addVariant(dress.id(), ClothingSize.P, Color.RED, "7892000000010", 2, 1);

        assertThat(catalog.listAllProducts()).hasSize(2);

        // --- 2. Customer 1 (Walk-in): Buys 3 polos -> qualifies for 15% Volume Discount ---
        OrderDTO order1 = sales.createOrder(null);
        sales.addItem(order1.orderId(), poloM.sku(), 2);
        sales.addItem(order1.orderId(), poloG.sku(), 1); // 3 items total = 3 * 150 = 450.00

        // Apply volume discount (3+ items -> 15% off)
        sales.applyDiscount(order1.orderId(), VolumeDiscountStrategy.of(3, 15.0));

        OrderDTO order1Summary = sales.findById(order1.orderId()).orElseThrow();
        assertThat(order1Summary.subtotal()).isEqualByComparingTo("450.00");
        assertThat(order1Summary.discount()).isEqualByComparingTo("67.50"); // 15% of 450
        assertThat(order1Summary.total()).isEqualByComparingTo("382.50");

        // Checkout with PIX
        OrderDTO paidOrder1 = sales.checkout(order1.orderId(), PaymentMethod.PIX, Money.of(382.50));
        assertThat(paidOrder1.status()).isEqualTo("Pago");

        // --- 3. Customer 2 (VIP with CPF): Buys 1 dress -> 10% Promotional Coupon ---
        OrderDTO order2 = sales.createOrder("12345678909");
        sales.addItem(order2.orderId(), dressP.sku(), 1);
        sales.applyDiscount(order2.orderId(), PercentageDiscountStrategy.of(10.0));

        OrderDTO order2Summary = sales.findById(order2.orderId()).orElseThrow();
        assertThat(order2Summary.subtotal()).isEqualByComparingTo("400.00");
        assertThat(order2Summary.discount()).isEqualByComparingTo("40.00");
        assertThat(order2Summary.total()).isEqualByComparingTo("360.00");

        // Checkout with Credit Card
        OrderDTO paidOrder2 = sales.checkout(order2.orderId(), PaymentMethod.CREDIT_CARD, Money.of(360.00));
        assertThat(paidOrder2.status()).isEqualTo("Pago");

        // --- 4. Verify Stock Levels after Sales ---
        ProductDTO updatedPolo = catalog.findBySku(poloM.sku()).orElseThrow();
        VariantDTO updatedPoloM = updatedPolo.variants().stream().filter(v -> v.sku().equals(poloM.sku())).findFirst().orElseThrow();
        VariantDTO updatedPoloG = updatedPolo.variants().stream().filter(v -> v.sku().equals(poloG.sku())).findFirst().orElseThrow();

        assertThat(updatedPoloM.stockQuantity()).isEqualTo(3); // 5 - 2 = 3
        assertThat(updatedPoloG.stockQuantity()).isEqualTo(2); // 3 - 1 = 2 (hit minimum threshold!)
        assertThat(updatedPoloG.lowStock()).isTrue();

        ProductDTO updatedDress = catalog.findBySku(dressP.sku()).orElseThrow();
        VariantDTO updatedDressP = updatedDress.variants().stream().filter(v -> v.sku().equals(dressP.sku())).findFirst().orElseThrow();
        assertThat(updatedDressP.stockQuantity()).isEqualTo(1); // 2 - 1 = 1 (hit minimum threshold!)
        assertThat(updatedDressP.lowStock()).isTrue();

        // --- 5. Verify Domain Events Published ---
        assertThat(paidEvents).hasSize(2);
        assertThat(paidEvents.get(0).totalAmount()).isEqualTo(Money.of(382.50));
        assertThat(paidEvents.get(1).totalAmount()).isEqualTo(Money.of(360.00));
        assertThat(lowStockEvents).isNotEmpty();

        // --- 6. Cash Register & Store Revenue Verification ---
        Money expectedRevenue = Money.of(382.50).add(Money.of(360.00)); // 742.50
        assertThat(sales.calculateTotalRevenue()).isEqualTo(expectedRevenue);
    }
}
