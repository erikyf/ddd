package com.exemplo.domain.customer.model;

import com.exemplo.domain.shared.DomainValidationException;

import java.util.Objects;

/**
 * Value Object representing a Brazilian CPF (Cadastro de Pessoas Físicas).
 * Validates check digits via Modulo 11 algorithm.
 */
public final class CPF {

    private final String value;

    public CPF(String rawValue) {
        if (rawValue == null || rawValue.trim().isEmpty()) {
            throw new DomainValidationException("CPF cannot be blank");
        }
        String digits = rawValue.replaceAll("\\D", "");
        if (digits.length() != 11) {
            throw new DomainValidationException("CPF must contain exactly 11 digits, but got: " + digits.length());
        }
        if (isAllSameDigit(digits)) {
            throw new DomainValidationException("CPF cannot contain all identical digits: " + rawValue);
        }
        if (!hasValidCheckDigits(digits)) {
            throw new DomainValidationException("Invalid CPF check digits: " + rawValue);
        }
        this.value = digits;
    }

    public static CPF of(String value) {
        return new CPF(value);
    }

    private static boolean isAllSameDigit(String s) {
        char first = s.charAt(0);
        for (int i = 1; i < s.length(); i++) {
            if (s.charAt(i) != first) return false;
        }
        return true;
    }

    private static boolean hasValidCheckDigits(String digits) {
        // Calculate first check digit
        int sum1 = 0;
        for (int i = 0; i < 9; i++) {
            sum1 += (digits.charAt(i) - '0') * (10 - i);
        }
        int remainder1 = 11 - (sum1 % 11);
        int digit1 = (remainder1 >= 10) ? 0 : remainder1;

        if ((digits.charAt(9) - '0') != digit1) {
            return false;
        }

        // Calculate second check digit
        int sum2 = 0;
        for (int i = 0; i < 10; i++) {
            sum2 += (digits.charAt(i) - '0') * (11 - i);
        }
        int remainder2 = 11 - (sum2 % 11);
        int digit2 = (remainder2 >= 10) ? 0 : remainder2;

        return (digits.charAt(10) - '0') == digit2;
    }

    public String getValue() {
        return value;
    }

    public String formatted() {
        return String.format("%s.%s.%s-%s",
                value.substring(0, 3),
                value.substring(3, 6),
                value.substring(6, 9),
                value.substring(9, 11)
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CPF cpf)) return false;
        return Objects.equals(value, cpf.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return formatted();
    }
}
