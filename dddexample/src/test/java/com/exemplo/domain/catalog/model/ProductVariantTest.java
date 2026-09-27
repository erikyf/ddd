package com.exemplo.domain.catalog.model;

import com.exemplo.domain.shared.DomainValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductVariantTest {

    private SKU sku;
    private Color navyColor;

    @BeforeEach
    void setUp() {
        sku = SKU.of("SHIRT-NAVY-M");
        navyColor = Color.NAVY;
    }

    @Test
    @DisplayName("Should create variant with valid parameters")
    void shouldCreateVariantSuccessfully() {
        ProductVariant variant = new ProductVariant(sku, ClothingSize.M, navyColor, Barcode.of("7891000200030"), 10, 3);

        assertThat(variant.getSku()).isEqualTo(sku);
        assertThat(variant.getSize()).isEqualTo(ClothingSize.M);
        assertThat(variant.getColor()).isEqualTo(navyColor);
        assertThat(variant.getStockQuantity()).isEqualTo(10);
        assertThat(variant.getMinStockThreshold()).isEqualTo(3);
        assertThat(variant.isInStock()).isTrue();
        assertThat(variant.isLowStock()).isFalse();
    }

    @Test
    @DisplayName("Should reject negative initial stock or threshold")
    void shouldRejectNegativeStockValues() {
        assertThatThrownBy(() -> new ProductVariant(sku, ClothingSize.M, navyColor, null, -1, 3))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("Initial stock cannot be negative");

        assertThatThrownBy(() -> new ProductVariant(sku, ClothingSize.M, navyColor, null, 5, -2))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("threshold cannot be negative");
    }

    @Test
    @DisplayName("Should replenish stock when valid quantity added")
    void shouldAddStock() {
        ProductVariant variant = new ProductVariant(sku, ClothingSize.M, navyColor, null, 10, 3);
        variant.addStock(5);
        assertThat(variant.getStockQuantity()).isEqualTo(15);
    }

    @Test
    @DisplayName("Should throw exception when adding non-positive stock")
    void shouldThrowWhenAddingNonPositiveStock() {
        ProductVariant variant = new ProductVariant(sku, ClothingSize.M, navyColor, null, 10, 3);
        assertThatThrownBy(() -> variant.addStock(0))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("must be positive");
    }

    @Test
    @DisplayName("Should deduct stock successfully")
    void shouldDeductStock() {
        ProductVariant variant = new ProductVariant(sku, ClothingSize.M, navyColor, null, 10, 3);
        variant.deductStock(4);
        assertThat(variant.getStockQuantity()).isEqualTo(6);
    }

    @Test
    @DisplayName("Should throw exception when deducting more stock than available")
    void shouldThrowOnInsufficientStock() {
        ProductVariant variant = new ProductVariant(sku, ClothingSize.M, navyColor, null, 3, 2);
        assertThatThrownBy(() -> variant.deductStock(5))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("Insufficient stock for SKU");
    }

    @Test
    @DisplayName("Should identify low stock when stock drops to or below threshold")
    void shouldIdentifyLowStock() {
        ProductVariant variant = new ProductVariant(sku, ClothingSize.M, navyColor, null, 3, 3);
        assertThat(variant.isLowStock()).isTrue();

        variant.deductStock(1);
        assertThat(variant.getStockQuantity()).isEqualTo(2);
        assertThat(variant.isLowStock()).isTrue();
    }
}
