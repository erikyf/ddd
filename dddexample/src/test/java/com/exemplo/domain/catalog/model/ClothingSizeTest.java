package com.exemplo.domain.catalog.model;

import com.exemplo.domain.shared.DomainValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ClothingSizeTest {

    @ParameterizedTest
    @ValueSource(strings = {"PP", "p", "M", "g", "GG", "xg", "un"})
    @DisplayName("Should parse letter sizes case-insensitively")
    void shouldParseLetterSizes(String input) {
        ClothingSize size = ClothingSize.fromString(input);
        assertThat(size).isNotNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {"36", "38", "40", "42", "44", "46", "48", "50", "52"})
    @DisplayName("Should parse numeric garment sizes")
    void shouldParseNumericSizes(String input) {
        ClothingSize size = ClothingSize.fromString(input);
        assertThat(size.getCode()).isEqualTo(input);
    }

    @Test
    @DisplayName("Should throw exception for unknown sizes")
    void shouldThrowForUnknownSize() {
        assertThatThrownBy(() -> ClothingSize.fromString("XXXL"))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("Invalid clothing size");
    }

    @Test
    @DisplayName("Should throw exception for blank size")
    void shouldThrowForBlankSize() {
        assertThatThrownBy(() -> ClothingSize.fromString("   "))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("cannot be blank");
    }
}
