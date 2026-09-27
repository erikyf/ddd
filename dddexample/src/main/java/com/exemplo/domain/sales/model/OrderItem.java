package com.exemplo.domain.sales.model;

import com.exemplo.domain.catalog.model.SKU;
import com.exemplo.domain.shared.DomainValidationException;
import com.exemplo.domain.shared.Money;

import java.util.Objects;

/**
 * Entity representing an individual line item within a SaleOrder aggregate.
 */
public class OrderItem {

    private final SKU sku;
    private final String productName;
    private final Money unitPrice;
    private int quantity;

    public OrderItem(SKU sku, String productName, Money unitPrice, int quantity) {
        if (sku == null) {
            throw new DomainValidationException("OrderItem SKU cannot be null");
        }
        if (productName == null || productName.trim().isEmpty()) {
            throw new DomainValidationException("OrderItem product name cannot be blank");
        }
        if (unitPrice == null || !unitPrice.isPositive()) {
            throw new DomainValidationException("OrderItem unit price must be greater than zero");
        }
        if (quantity <= 0) {
            throw new DomainValidationException("OrderItem quantity must be greater than zero, but was: " + quantity);
        }

        this.sku = sku;
        this.productName = productName.trim();
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public SKU getSku() {
        return sku;
    }

    public String getProductName() {
        return productName;
    }

    public Money getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void addQuantity(int amount) {
        if (amount <= 0) {
            throw new DomainValidationException("Amount to add must be positive, but was: " + amount);
        }
        this.quantity += amount;
    }

    public Money calculateSubtotal() {
        return unitPrice.multiply(quantity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderItem orderItem)) return false;
        return Objects.equals(sku, orderItem.sku);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sku);
    }

    @Override
    public String toString() {
        return String.format("%s (%s) x%d = %s", productName, sku.getValue(), quantity, calculateSubtotal().formatted());
    }
}
