package com.exemplo.domain.catalog.model;

import com.exemplo.domain.shared.DomainValidationException;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object representing a clothing color with optional hex code.
 */
public final class Color {

    private static final Pattern HEX_PATTERN = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");

    public static final Color BLACK = new Color("Preto", "#000000");
    public static final Color WHITE = new Color("Branco", "#FFFFFF");
    public static final Color RED = new Color("Vermelho", "#FF0000");
    public static final Color BLUE = new Color("Azul", "#0000FF");
    public static final Color NAVY = new Color("Azul Marinho", "#000080");
    public static final Color GREEN = new Color("Verde", "#008000");
    public static final Color BEIGE = new Color("Bege", "#F5F5DC");
    public static final Color GRAY = new Color("Cinza", "#808080");

    private final String name;
    private final String hexCode;

    public Color(String name, String hexCode) {
        if (name == null || name.trim().isEmpty()) {
            throw new DomainValidationException("Color name cannot be blank");
        }
        this.name = name.trim();
        if (hexCode != null && !hexCode.trim().isEmpty()) {
            String trimmedHex = hexCode.trim();
            if (!HEX_PATTERN.matcher(trimmedHex).matches()) {
                throw new DomainValidationException("Invalid color hex code: " + hexCode);
            }
            this.hexCode = trimmedHex.toUpperCase();
        } else {
            this.hexCode = null;
        }
    }

    public static Color of(String name) {
        return new Color(name, null);
    }

    public static Color of(String name, String hexCode) {
        return new Color(name, hexCode);
    }

    public String getName() {
        return name;
    }

    public String getHexCode() {
        return hexCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Color color)) return false;
        return name.equalsIgnoreCase(color.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name.toLowerCase());
    }

    @Override
    public String toString() {
        return hexCode != null ? name + " (" + hexCode + ")" : name;
    }
}
