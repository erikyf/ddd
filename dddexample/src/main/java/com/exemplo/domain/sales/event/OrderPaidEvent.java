package com.exemplo.domain.sales.event;

import com.exemplo.domain.sales.model.OrderId;
import com.exemplo.domain.sales.model.PaymentMethod;
import com.exemplo.domain.shared.Money;
import com.exemplo.domain.shared.event.DomainEvent;

import java.time.Instant;
import java.util.Objects;

/**
 * Domain Event emitted when a customer order is successfully paid.
 */
public record OrderPaidEvent(
        OrderId orderId,
        Money totalAmount,
        PaymentMethod paymentMethod,
        int totalItems,
        Instant occurredOn
) implements DomainEvent {

    public OrderPaidEvent(OrderId orderId, Money totalAmount, PaymentMethod paymentMethod, int totalItems) {
        this(
                Objects.requireNonNull(orderId, "orderId cannot be null"),
                Objects.requireNonNull(totalAmount, "totalAmount cannot be null"),
                Objects.requireNonNull(paymentMethod, "paymentMethod cannot be null"),
                totalItems,
                Instant.now()
        );
    }
}
