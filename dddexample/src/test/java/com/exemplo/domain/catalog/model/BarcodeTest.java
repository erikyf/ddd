package com.exemplo.domain.catalog.model;

import com.exemplo.domain.shared.DomainValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BarcodeTest {

    @Test
    @DisplayName("Should accept valid EAN-13 barcode")
    void shouldAcceptValidBarcode() {
        Barcode barcode = Barcode.of("7891234567890");
        assertThat(barcode.getValue()).isEqualTo("7891234567890");
    }

    @Test
    @DisplayName("Should reject blank barcode")
    void shouldRejectBlankBarcode() {
        assertThatThrownBy(() -> Barcode.of("   "))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("cannot be blank");
    }

    @Test
    @DisplayName("Should reject too short or too long barcode")
    void shouldRejectInvalidLength() {
        assertThatThrownBy(() -> Barcode.of("123"))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("Invalid barcode format");

        assertThatThrownBy(() -> Barcode.of("12345678901234567890"))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("Invalid barcode format");
    }
}
