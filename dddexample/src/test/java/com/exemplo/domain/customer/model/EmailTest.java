package com.exemplo.domain.customer.model;

import com.exemplo.domain.shared.DomainValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmailTest {

    @Test
    @DisplayName("Should accept valid email and normalize to lowercase")
    void shouldAcceptValidEmail() {
        Email email = Email.of("  Cliente.MODA@EXEMPLO.COM  ");
        assertThat(email.getValue()).isEqualTo("cliente.moda@exemplo.com");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "invalid-email",
            "@missinguser.com",
            "missingdomain@",
            "spaces in@domain.com",
            "no-tld@domain"
    })
    @DisplayName("Should reject malformed emails")
    void shouldRejectMalformedEmail(String raw) {
        assertThatThrownBy(() -> Email.of(raw))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("Invalid email format");
    }

    @Test
    @DisplayName("Should reject blank email")
    void shouldRejectBlankEmail() {
        assertThatThrownBy(() -> Email.of(""))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("cannot be blank");
    }
}
