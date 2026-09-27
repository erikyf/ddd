package com.exemplo.domain.catalog.model;

import com.exemplo.domain.shared.DomainValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ColorTest {

    @Test
    @DisplayName("Should create color with name only")
    void shouldCreateColorWithName() {
        Color color = Color.of("Azul");
        assertThat(color.getName()).isEqualTo("Azul");
        assertThat(color.getHexCode()).isNull();
        assertThat(color.toString()).isEqualTo("Azul");
    }

    @Test
    @DisplayName("Should create color with name and valid hex code")
    void shouldCreateColorWithHex() {
        Color color = Color.of("Preto", "#000000");
        assertThat(color.getName()).isEqualTo("Preto");
        assertThat(color.getHexCode()).isEqualTo("#000000");
        assertThat(color.toString()).isEqualTo("Preto (#000000)");
    }

    @Test
    @DisplayName("Should equate colors ignoring case in name")
    void shouldEquateColorsByNameCaseInsensitive() {
        Color c1 = Color.of("Azul Marinho");
        Color c2 = Color.of("azul marinho");
        assertThat(c1).isEqualTo(c2);
        assertThat(c1.hashCode()).isEqualTo(c2.hashCode());
    }

    @Test
    @DisplayName("Should throw exception when color name is blank")
    void shouldThrowWhenNameIsBlank() {
        assertThatThrownBy(() -> Color.of(""))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("Color name cannot be blank");
    }

    @Test
    @DisplayName("Should throw exception when hex code is invalid")
    void shouldThrowWhenHexCodeIsInvalid() {
        assertThatThrownBy(() -> Color.of("Azul", "123456"))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("Invalid color hex code");

        assertThatThrownBy(() -> Color.of("Azul", "#GGFFFF"))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("Invalid color hex code");
    }

    @Test
    @DisplayName("Predefined colors should be accessible")
    void shouldAccessPredefinedColors() {
        assertThat(Color.BLACK.getName()).isEqualTo("Preto");
        assertThat(Color.WHITE.getName()).isEqualTo("Branco");
        assertThat(Color.NAVY.getHexCode()).isEqualTo("#000080");
    }
}
