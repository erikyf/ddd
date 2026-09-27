package com.exemplo.domain.shared;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MoneyTest {

    @Test
    @DisplayName("Should create Money from double and round to 2 decimal places")
    void shouldCreateFromDouble() {
        Money money = Money.of(129.90);
        assertThat(money.getAmount()).isEqualByComparingTo("129.90");
        assertThat(money.getCurrency()).isEqualTo(Money.BRL);
    }

    @Test
    @DisplayName("Should create Money from string with comma or dot")
    void shouldCreateFromString() {
        Money m1 = Money.of("89.99");
        Money m2 = Money.of("89,99");
        assertThat(m1).isEqualTo(m2);
        assertThat(m1.getAmount()).isEqualByComparingTo("89.99");
    }

    @Test
    @DisplayName("Should create Money from cents")
    void shouldCreateFromCents() {
        Money money = Money.ofCents(4990);
        assertThat(money.getAmount()).isEqualByComparingTo("49.90");
    }

    @Test
    @DisplayName("Should throw exception when creating from invalid string")
    void shouldThrowOnInvalidString() {
        assertThatThrownBy(() -> Money.of("abc"))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("Invalid numeric string");

        assertThatThrownBy(() -> Money.of("   "))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("cannot be blank");
    }

    @Test
    @DisplayName("Should add money correctly")
    void shouldAddMoney() {
        Money m1 = Money.of(50.00);
        Money m2 = Money.of(25.50);
        Money sum = m1.add(m2);
        assertThat(sum.getAmount()).isEqualByComparingTo("75.50");
    }

    @Test
    @DisplayName("Should subtract money correctly")
    void shouldSubtractMoney() {
        Money m1 = Money.of(100.00);
        Money m2 = Money.of(35.25);
        Money result = m1.subtract(m2);
        assertThat(result.getAmount()).isEqualByComparingTo("64.75");
    }

    @Test
    @DisplayName("Should throw exception when adding money of different currencies")
    void shouldThrowOnCurrencyMismatch() {
        Money brl = Money.of(100.00, Currency.getInstance("BRL"));
        Money usd = Money.of(100.00, Currency.getInstance("USD"));

        assertThatThrownBy(() -> brl.add(usd))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("Currency mismatch");
    }

    @Test
    @DisplayName("Should multiply money by integer quantity")
    void shouldMultiplyByQuantity() {
        Money unitPrice = Money.of(79.90);
        Money total = unitPrice.multiply(3);
        assertThat(total.getAmount()).isEqualByComparingTo("239.70");
    }

    @Test
    @DisplayName("Should calculate percentage correctly")
    void shouldCalculatePercentage() {
        Money price = Money.of(200.00);
        Money discount = price.percentage(BigDecimal.valueOf(15)); // 15%
        assertThat(discount.getAmount()).isEqualByComparingTo("30.00");
    }

    @Test
    @DisplayName("Should compare money values properly")
    void shouldCompareMoneyValues() {
        Money cheap = Money.of(30.00);
        Money expensive = Money.of(150.00);
        Money sameAsCheap = Money.of(30.00);

        assertThat(expensive.isGreaterThan(cheap)).isTrue();
        assertThat(cheap.isLessThan(expensive)).isTrue();
        assertThat(cheap.isGreaterThanOrEqual(sameAsCheap)).isTrue();
        assertThat(cheap.compareTo(expensive)).isNegative();
    }

    @Test
    @DisplayName("Should check positive, zero, and negative states")
    void shouldCheckStates() {
        assertThat(Money.of(10.0).isPositive()).isTrue();
        assertThat(Money.zero().isZero()).isTrue();
        assertThat(Money.of(-5.0).isNegative()).isTrue();
    }

    @Test
    @DisplayName("Should format money with Brazilian currency symbol")
    void shouldFormatCurrency() {
        Money money = Money.of(149.90);
        String formatted = money.formatted();
        assertThat(formatted).contains("149,90");
    }
}
