package com.exemplo.domain.catalog.model;

import com.exemplo.domain.shared.DomainValidationException;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object representing a Stock Keeping Unit (SKU) identifying a specific garment variant.
 */
public final class SKU {

    private static final Pattern SKU_PATTERN = Pattern.compile("^[A-Z0-9_.-]{3,40}$");

    private final String value;

    public SKU(String rawValue) {
        if (rawValue == null || rawValue.trim().isEmpty()) {
            throw new DomainValidationException("SKU cannot be blank");
        }
        String normalized = rawValue.trim().toUpperCase();
        if (!SKU_PATTERN.matcher(normalized).matches()) {
            throw new DomainValidationException(
                    "Invalid SKU format: '" + rawValue + "'. Must be 3-40 alphanumeric characters, hyphens, or underscores."
            );
        }
        this.value = normalized;
    }

    public static SKU of(String value) {
        return new SKU(value);
    }

    public static SKU generate(String prefix, String modelCode, Color color, ClothingSize size) {
        if (prefix == null || modelCode == null || color == null || size == null) {
            throw new DomainValidationException("Cannot generate SKU with null parameters");
        }
        String cleanPrefix = prefix.replaceAll("[^A-Za-z0-9]", "").toUpperCase();
        String cleanModel = modelCode.replaceAll("[^A-Za-z0-9]", "").toUpperCase();
        String cleanColor = color.getName().replaceAll("[^A-Za-z0-9]", "").toUpperCase();
        if (cleanColor.length() > 3) {
            cleanColor = cleanColor.substring(0, 3);
        }
        return new SKU(String.format("%s-%s-%s-%s", cleanPrefix, cleanModel, cleanColor, size.getCode()));
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SKU sku)) return false;
        return Objects.equals(value, sku.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
