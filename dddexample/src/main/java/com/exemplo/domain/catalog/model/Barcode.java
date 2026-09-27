package com.exemplo.domain.catalog.model;

import com.exemplo.domain.shared.DomainValidationException;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object representing a product barcode (e.g. EAN-13 or code-128).
 */
public final class Barcode {

    private static final Pattern BARCODE_PATTERN = Pattern.compile("^[0-9A-Za-z]{8,18}$");

    private final String value;

    public Barcode(String rawValue) {
        if (rawValue == null || rawValue.trim().isEmpty()) {
            throw new DomainValidationException("Barcode cannot be blank");
        }
        String normalized = rawValue.trim().toUpperCase();
        if (!BARCODE_PATTERN.matcher(normalized).matches()) {
            throw new DomainValidationException("Invalid barcode format: " + rawValue + ". Expected 8 to 18 alphanumeric characters.");
        }
        this.value = normalized;
    }

    public static Barcode of(String value) {
        return new Barcode(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Barcode barcode)) return false;
        return Objects.equals(value, barcode.value);
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
