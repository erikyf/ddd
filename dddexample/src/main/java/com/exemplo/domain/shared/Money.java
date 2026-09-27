package com.exemplo.domain.shared;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
import java.util.Objects;

/**
 * Value Object representing a monetary amount with a specific currency.
 * Immutable, self-validating, and encapsulates monetary arithmetic.
 */
public final class Money implements Comparable<Money> {

    public static final Currency BRL = Currency.getInstance("BRL");
    public static final Locale BRAZIL_LOCALE = new Locale("pt", "BR");

    private final BigDecimal amount;
    private final Currency currency;

    private Money(BigDecimal amount, Currency currency) {
        if (amount == null) {
            throw new DomainValidationException("Amount cannot be null");
        }
        if (currency == null) {
            throw new DomainValidationException("Currency cannot be null");
        }
        this.amount = amount.setScale(2, RoundingMode.HALF_EVEN);
        this.currency = currency;
    }

    public static Money of(BigDecimal amount) {
        return new Money(amount, BRL);
    }

    public static Money of(BigDecimal amount, Currency currency) {
        return new Money(amount, currency);
    }

    public static Money of(double amount, Currency currency) {
        return new Money(BigDecimal.valueOf(amount), currency);
    }

    public static Money of(double amount) {
        return new Money(BigDecimal.valueOf(amount), BRL);
    }

    public static Money of(String amount) {
        if (amount == null || amount.trim().isEmpty()) {
            throw new DomainValidationException("Amount string cannot be blank");
        }
        try {
            return new Money(new BigDecimal(amount.trim().replace(',', '.')), BRL);
        } catch (NumberFormatException e) {
            throw new DomainValidationException("Invalid numeric string for Money: " + amount);
        }
    }

    public static Money ofCents(long cents) {
        return new Money(BigDecimal.valueOf(cents, 2), BRL);
    }

    public static Money zero() {
        return new Money(BigDecimal.ZERO, BRL);
    }

    public static Money zero(Currency currency) {
        return new Money(BigDecimal.ZERO, currency);
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public Money add(Money other) {
        assertSameCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }

    public Money subtract(Money other) {
        assertSameCurrency(other);
        return new Money(this.amount.subtract(other.amount), this.currency);
    }

    public Money multiply(int quantity) {
        return multiply(BigDecimal.valueOf(quantity));
    }

    public Money multiply(BigDecimal factor) {
        if (factor == null) {
            throw new DomainValidationException("Multiplication factor cannot be null");
        }
        return new Money(this.amount.multiply(factor), this.currency);
    }

    public Money percentage(BigDecimal percent) {
        if (percent == null) {
            throw new DomainValidationException("Percentage cannot be null");
        }
        BigDecimal fraction = percent.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_EVEN);
        return new Money(this.amount.multiply(fraction), this.currency);
    }

    public boolean isPositive() {
        return this.amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isZero() {
        return this.amount.compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean isNegative() {
        return this.amount.compareTo(BigDecimal.ZERO) < 0;
    }

    public boolean isGreaterThan(Money other) {
        assertSameCurrency(other);
        return this.compareTo(other) > 0;
    }

    public boolean isGreaterThanOrEqual(Money other) {
        assertSameCurrency(other);
        return this.compareTo(other) >= 0;
    }

    public boolean isLessThan(Money other) {
        assertSameCurrency(other);
        return this.compareTo(other) < 0;
    }

    public boolean isLessThanOrEqual(Money other) {
        assertSameCurrency(other);
        return this.compareTo(other) <= 0;
    }

    private void assertSameCurrency(Money other) {
        if (other == null) {
            throw new DomainValidationException("Cannot compare or operate on null Money");
        }
        if (!this.currency.equals(other.currency)) {
            throw new DomainValidationException(String.format(
                    "Currency mismatch: Cannot operate between %s and %s",
                    this.currency.getCurrencyCode(),
                    other.currency.getCurrencyCode()
            ));
        }
    }

    public String formatted() {
        NumberFormat format = NumberFormat.getCurrencyInstance(BRAZIL_LOCALE);
        format.setCurrency(currency);
        return format.format(amount);
    }

    @Override
    public int compareTo(Money other) {
        assertSameCurrency(other);
        return this.amount.compareTo(other.amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money money)) return false;
        return Objects.equals(amount, money.amount) && Objects.equals(currency, money.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }

    @Override
    public String toString() {
        return currency.getCurrencyCode() + " " + amount;
    }
}
