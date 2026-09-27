package com.exemplo.infrastructure.persistence;

import com.exemplo.domain.catalog.model.SKU;
import com.exemplo.domain.customer.model.CPF;
import com.exemplo.domain.sales.model.OrderId;
import com.exemplo.domain.sales.model.OrderStatus;
import com.exemplo.domain.sales.model.SaleOrder;
import com.exemplo.domain.shared.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class InMemorySaleOrderRepositoryTest {

    private InMemorySaleOrderRepository repository;
    private SaleOrder order1;
    private SaleOrder order2;
    private CPF customerCpf;

    @BeforeEach
    void setUp() {
        repository = new InMemorySaleOrderRepository();
        customerCpf = CPF.of("12345678909");

        order1 = SaleOrder.create(OrderId.of("ORD-1"), customerCpf);
        order1.addItem(SKU.of("SKU-1"), "Item 1", Money.of(100.0), 1);

        order2 = SaleOrder.create(OrderId.of("ORD-2"), null);
        order2.addItem(SKU.of("SKU-2"), "Item 2", Money.of(200.0), 1);
        order2.confirm();

        repository.save(order1);
        repository.save(order2);
    }

    @Test
    @DisplayName("Should find order by OrderId")
    void shouldFindById() {
        Optional<SaleOrder> found = repository.findById(OrderId.of("ORD-1"));
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(OrderId.of("ORD-1"));
    }

    @Test
    @DisplayName("Should find orders by status")
    void shouldFindByStatus() {
        List<SaleOrder> drafts = repository.findByStatus(OrderStatus.DRAFT);
        assertThat(drafts).containsExactly(order1);

        List<SaleOrder> confirmed = repository.findByStatus(OrderStatus.CONFIRMED);
        assertThat(confirmed).containsExactly(order2);
    }

    @Test
    @DisplayName("Should find orders by customer CPF")
    void shouldFindByCustomerCpf() {
        List<SaleOrder> customerOrders = repository.findByCustomerCpf(customerCpf);
        assertThat(customerOrders).containsExactly(order1);
    }

    @Test
    @DisplayName("Should delete order")
    void shouldDeleteOrder() {
        assertThat(repository.existsById(OrderId.of("ORD-1"))).isTrue();

        repository.delete(OrderId.of("ORD-1"));

        assertThat(repository.existsById(OrderId.of("ORD-1"))).isFalse();
        assertThat(repository.findById(OrderId.of("ORD-1"))).isEmpty();
    }
}
