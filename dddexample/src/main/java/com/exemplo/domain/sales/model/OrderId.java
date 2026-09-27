package com.exemplo.domain.sales.model;

import com.exemplo.domain.shared.DomainValidationException;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object representing a unique identifier for a SaleOrder.
 */
public final class OrderId {

    private final String value;

    public OrderId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new DomainValidationException("OrderId cannot be blank");
        }
        this.value = value.trim();
    }

    public static OrderId generate() {
        return new OrderId(UUID.randomUUID().toString());
    }

    public static OrderId of(String value) {
        return new OrderId(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderId orderId)) return false;
        return Objects.equals(value, orderId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
