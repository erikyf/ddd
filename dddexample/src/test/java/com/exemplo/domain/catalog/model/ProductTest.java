package com.exemplo.domain.catalog.model;

import com.exemplo.domain.shared.DomainValidationException;
import com.exemplo.domain.shared.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductTest {

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product(
                ProductId.of("PROD-001"),
                "Camisa Linho",
                "Dudalina",
                ClothingCategory.SHIRTS,
                Money.of(259.90)
        );
    }

    @Test
    @DisplayName("Should create product with valid attributes")
    void shouldCreateProduct() {
        assertThat(product.getId().getValue()).isEqualTo("PROD-001");
        assertThat(product.getName()).isEqualTo("Camisa Linho");
        assertThat(product.getBrand()).isEqualTo("Dudalina");
        assertThat(product.getCategory()).isEqualTo(ClothingCategory.SHIRTS);
        assertThat(product.getBasePrice()).isEqualTo(Money.of(259.90));
        assertThat(product.getVariants()).isEmpty();
    }

    @Test
    @DisplayName("Should reject blank name or brand or zero price")
    void shouldRejectInvalidAttributes() {
        assertThatThrownBy(() -> new Product(ProductId.of("1"), "", "Brand", ClothingCategory.SHIRTS, Money.of(100)))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("name cannot be blank");

        assertThatThrownBy(() -> new Product(ProductId.of("1"), "Name", "   ", ClothingCategory.SHIRTS, Money.of(100)))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("brand cannot be blank");

        assertThatThrownBy(() -> new Product(ProductId.of("1"), "Name", "Brand", ClothingCategory.SHIRTS, Money.zero()))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("Base price must be greater than zero");
    }

    @Test
    @DisplayName("Should add variant successfully and generate SKU")
    void shouldAddVariant() {
        ProductVariant variant = product.addVariant(ClothingSize.M, Color.NAVY, Barcode.of("7891234567890"), 12, 4);

        assertThat(variant).isNotNull();
        assertThat(product.getVariants()).hasSize(1);
        assertThat(product.getTotalStock()).isEqualTo(12);
        assertThat(product.hasVariant(variant.getSku())).isTrue();
    }

    @Test
    @DisplayName("Should reject duplicate variant with same Size and Color")
    void shouldRejectDuplicateVariant() {
        product.addVariant(ClothingSize.M, Color.NAVY, null, 10, 2);

        assertThatThrownBy(() -> product.addVariant(ClothingSize.M, Color.NAVY, null, 5, 2))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    @DisplayName("Should replenish and deduct stock via aggregate root")
    void shouldManageStockViaAggregate() {
        ProductVariant variant = product.addVariant(ClothingSize.G, Color.WHITE, null, 8, 3);
        SKU sku = variant.getSku();

        product.replenishStock(sku, 5);
        assertThat(product.getVariant(sku).getStockQuantity()).isEqualTo(13);

        product.deductStock(sku, 6);
        assertThat(product.getVariant(sku).getStockQuantity()).isEqualTo(7);
    }

    @Test
    @DisplayName("Should detect low stock across variants")
    void shouldDetectLowStock() {
        ProductVariant v1 = product.addVariant(ClothingSize.P, Color.BLACK, null, 10, 2);
        ProductVariant v2 = product.addVariant(ClothingSize.M, Color.BLACK, null, 2, 3); // low stock

        assertThat(product.hasLowStock()).isTrue();
        assertThat(product.getLowStockVariants()).containsExactly(v2);
    }

    @Test
    @DisplayName("Should update base price correctly")
    void shouldUpdateBasePrice() {
        product.updateBasePrice(Money.of(289.90));
        assertThat(product.getBasePrice()).isEqualTo(Money.of(289.90));

        assertThatThrownBy(() -> product.updateBasePrice(Money.zero()))
                .isInstanceOf(DomainValidationException.class);
    }
}
