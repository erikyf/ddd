package com.exemplo.domain.catalog.model;

import com.exemplo.domain.shared.DomainValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SKUTest {

    @Test
    @DisplayName("Should create valid SKU and normalize to uppercase")
    void shouldCreateValidSKU() {
        SKU sku = SKU.of("cam-azul-m-01");
        assertThat(sku.getValue()).isEqualTo("CAM-AZUL-M-01");
    }

    @Test
    @DisplayName("Should generate SKU from components")
    void shouldGenerateSKU() {
        SKU sku = SKU.generate("ZARA", "CAMISA-POLO", Color.of("Azul"), ClothingSize.M);
        assertThat(sku.getValue()).isEqualTo("ZARA-CAMISAPOLO-AZU-M");
    }

    @Test
    @DisplayName("Should throw exception for blank SKU")
    void shouldThrowForBlankSKU() {
        assertThatThrownBy(() -> SKU.of("   "))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("cannot be blank");
    }

    @Test
    @DisplayName("Should throw exception for invalid characters or length")
    void shouldThrowForInvalidFormat() {
        assertThatThrownBy(() -> SKU.of("A!"))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("Invalid SKU format");

        assertThatThrownBy(() -> SKU.of("A".repeat(45)))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("Invalid SKU format");
    }

    @Test
    @DisplayName("Should satisfy equality and hashcode based on value")
    void shouldSatisfyEquality() {
        SKU s1 = SKU.of("CAM-001");
        SKU s2 = SKU.of("cam-001");
        assertThat(s1).isEqualTo(s2);
        assertThat(s1.hashCode()).isEqualTo(s2.hashCode());
    }
}
