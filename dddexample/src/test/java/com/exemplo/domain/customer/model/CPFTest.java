package com.exemplo.domain.customer.model;

import com.exemplo.domain.shared.DomainValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CPFTest {

    @Test
    @DisplayName("Should accept valid CPF with or without formatting")
    void shouldAcceptValidCPF() {
        CPF cpfFormatted = CPF.of("123.456.789-09");
        CPF cpfDigitsOnly = CPF.of("12345678909");

        assertThat(cpfFormatted.getValue()).isEqualTo("12345678909");
        assertThat(cpfFormatted.formatted()).isEqualTo("123.456.789-09");
        assertThat(cpfFormatted).isEqualTo(cpfDigitsOnly);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "00000000000",
            "11111111111",
            "22222222222",
            "99999999999"
    })
    @DisplayName("Should reject CPF with all identical digits")
    void shouldRejectAllIdenticalDigits(String cpf) {
        assertThatThrownBy(() -> CPF.of(cpf))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("all identical digits");
    }

    @Test
    @DisplayName("Should reject CPF with incorrect check digits")
    void shouldRejectInvalidCheckDigits() {
        assertThatThrownBy(() -> CPF.of("123.456.789-00"))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("Invalid CPF check digits");
    }

    @Test
    @DisplayName("Should reject CPF with invalid length")
    void shouldRejectInvalidLength() {
        assertThatThrownBy(() -> CPF.of("123456789"))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("must contain exactly 11 digits");
    }

    @Test
    @DisplayName("Should reject blank CPF")
    void shouldRejectBlankCPF() {
        assertThatThrownBy(() -> CPF.of("   "))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("cannot be blank");
    }
}
