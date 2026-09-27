package com.exemplo.domain.sales.model;

import com.exemplo.domain.shared.DomainValidationException;
import com.exemplo.domain.shared.Money;

import java.time.Instant;
import java.util.Objects;

/**
 * Value Object recording a confirmed payment for a SaleOrder.
 */
public final class PaymentRecord {

    private final PaymentMethod method;
    private final Money amount;
    private final Instant paidAt;

    public PaymentRecord(PaymentMethod method, Money amount, Instant paidAt) {
        if (method == null) {
            throw new DomainValidationException("Payment method cannot be null");
        }
        if (amount == null) {
            throw new DomainValidationException("Payment amount cannot be null");
        }
        if (!amount.isPositive()) {
            throw new DomainValidationException("Payment amount must be greater than zero");
        }
        this.method = method;
        this.amount = amount;
        this.paidAt = Objects.requireNonNullElseGet(paidAt, Instant::now);
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public Money getAmount() {
        return amount;
    }

    public Instant getPaidAt() {
        return paidAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PaymentRecord that)) return false;
        return method == that.method && Objects.equals(amount, that.amount) && Objects.equals(paidAt, that.paidAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, amount, paidAt);
    }

    @Override
    public String toString() {
        return String.format("%s - %s at %s", method.getDescription(), amount.formatted(), paidAt);
    }
}
