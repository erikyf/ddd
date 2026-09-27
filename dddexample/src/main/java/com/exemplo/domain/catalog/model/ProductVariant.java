package com.exemplo.domain.catalog.model;

import com.exemplo.domain.shared.DomainValidationException;

import java.util.Objects;

/**
 * Entity representing a specific variation of a product (Size, Color, Stock, SKU).
 * Invariants are strictly enforced at the aggregate boundary.
 */
public class ProductVariant {

    private final SKU sku;
    private final ClothingSize size;
    private final Color color;
    private final Barcode barcode;
    private int stockQuantity;
    private final int minStockThreshold;

    public ProductVariant(SKU sku, ClothingSize size, Color color, Barcode barcode, int initialStock, int minStockThreshold) {
        if (sku == null) {
            throw new DomainValidationException("SKU cannot be null");
        }
        if (size == null) {
            throw new DomainValidationException("ClothingSize cannot be null");
        }
        if (color == null) {
            throw new DomainValidationException("Color cannot be null");
        }
        if (initialStock < 0) {
            throw new DomainValidationException("Initial stock cannot be negative: " + initialStock);
        }
        if (minStockThreshold < 0) {
            throw new DomainValidationException("Minimum stock threshold cannot be negative: " + minStockThreshold);
        }

        this.sku = sku;
        this.size = size;
        this.color = color;
        this.barcode = barcode;
        this.stockQuantity = initialStock;
        this.minStockThreshold = minStockThreshold;
    }

    public SKU getSku() {
        return sku;
    }

    public ClothingSize getSize() {
        return size;
    }

    public Color getColor() {
        return color;
    }

    public Barcode getBarcode() {
        return barcode;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public int getMinStockThreshold() {
        return minStockThreshold;
    }

    public void addStock(int quantity) {
        if (quantity <= 0) {
            throw new DomainValidationException("Quantity to add must be positive, but was: " + quantity);
        }
        this.stockQuantity += quantity;
    }

    public void deductStock(int quantity) {
        if (quantity <= 0) {
            throw new DomainValidationException("Quantity to deduct must be positive, but was: " + quantity);
        }
        if (quantity > this.stockQuantity) {
            throw new DomainValidationException(String.format(
                    "Insufficient stock for SKU %s. Requested: %d, Available: %d",
                    sku.getValue(), quantity, this.stockQuantity
            ));
        }
        this.stockQuantity -= quantity;
    }

    public boolean isLowStock() {
        return this.stockQuantity <= this.minStockThreshold;
    }

    public boolean isInStock() {
        return this.stockQuantity > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductVariant that)) return false;
        return Objects.equals(sku, that.sku);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sku);
    }

    @Override
    public String toString() {
        return String.format("%s [%s, %s] Stock: %d", sku.getValue(), size.getCode(), color.getName(), stockQuantity);
    }
}
