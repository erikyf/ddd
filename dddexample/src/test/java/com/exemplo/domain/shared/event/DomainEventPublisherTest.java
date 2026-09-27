package com.exemplo.domain.shared.event;

import com.exemplo.domain.catalog.event.StockLowEvent;
import com.exemplo.domain.catalog.model.SKU;
import com.exemplo.domain.sales.event.OrderPaidEvent;
import com.exemplo.domain.sales.model.OrderId;
import com.exemplo.domain.sales.model.PaymentMethod;
import com.exemplo.domain.shared.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class DomainEventPublisherTest {

    @BeforeEach
    void setUp() {
        DomainEventPublisher.clearSubscribers();
    }

    @Test
    @DisplayName("Should publish and handle OrderPaidEvent via subscriber")
    void shouldHandleOrderPaidEvent() {
        AtomicReference<OrderPaidEvent> received = new AtomicReference<>();
        DomainEventPublisher.subscribe(OrderPaidEvent.class, received::set);

        OrderPaidEvent event = new OrderPaidEvent(OrderId.of("ORD-1"), Money.of(250.0), PaymentMethod.PIX, 2);
        DomainEventPublisher.publish(event);

        assertThat(received.get()).isNotNull();
        assertThat(received.get().orderId()).isEqualTo(OrderId.of("ORD-1"));
        assertThat(received.get().totalAmount()).isEqualTo(Money.of(250.0));
        assertThat(received.get().paymentMethod()).isEqualTo(PaymentMethod.PIX);
    }

    @Test
    @DisplayName("Should publish and handle StockLowEvent via subscriber")
    void shouldHandleStockLowEvent() {
        AtomicBoolean alertTriggered = new AtomicBoolean(false);
        DomainEventPublisher.subscribe(StockLowEvent.class, e -> {
            if (e.currentStock() <= e.minThreshold()) {
                alertTriggered.set(true);
            }
        });

        StockLowEvent event = new StockLowEvent(SKU.of("CAM-AZU-M"), 1, 2);
        DomainEventPublisher.publish(event);

        assertThat(alertTriggered.get()).isTrue();
    }
}
