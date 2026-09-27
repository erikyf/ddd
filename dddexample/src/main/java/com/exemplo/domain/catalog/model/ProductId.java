package com.exemplo.domain.catalog.model;

import com.exemplo.domain.shared.DomainValidationException;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object representing a unique Product identifier.
 */
public final class ProductId {

    private final String value;

    public ProductId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new DomainValidationException("ProductId cannot be blank");
        }
        this.value = value.trim();
    }

    public static ProductId generate() {
        return new ProductId(UUID.randomUUID().toString());
    }

    public static ProductId of(String value) {
        return new ProductId(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductId productId)) return false;
        return Objects.equals(value, productId.value);
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
