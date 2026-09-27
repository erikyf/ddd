package com.exemplo.domain.customer.model;

import com.exemplo.domain.shared.DomainValidationException;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object representing a validated customer Email address.
 */
public final class Email {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private final String value;

    public Email(String rawValue) {
        if (rawValue == null || rawValue.trim().isEmpty()) {
            throw new DomainValidationException("Email cannot be blank");
        }
        String normalized = rawValue.trim().toLowerCase();
        if (!EMAIL_PATTERN.matcher(normalized).matches()) {
            throw new DomainValidationException("Invalid email format: " + rawValue);
        }
        this.value = normalized;
    }

    public static Email of(String value) {
        return new Email(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email email)) return false;
        return Objects.equals(value, email.value);
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
