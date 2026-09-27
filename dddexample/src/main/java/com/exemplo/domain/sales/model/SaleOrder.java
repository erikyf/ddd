package com.exemplo.domain.sales.model;

import com.exemplo.domain.catalog.model.SKU;
import com.exemplo.domain.customer.model.CPF;
import com.exemplo.domain.sales.policy.DiscountStrategy;
import com.exemplo.domain.shared.DomainValidationException;
import com.exemplo.domain.shared.Money;

import java.time.Instant;
import java.util.*;

/**
 * Aggregate Root for sales and checkout in the store.
 * Coordinates line items, promotional pricing strategies, order status transitions, and payments.
 */
public class SaleOrder {

    private final OrderId id;
    private final CPF customerCpf;
    private final Map<SKU, OrderItem> items = new LinkedHashMap<>();
    private OrderStatus status;
    private DiscountStrategy discountStrategy;
    private PaymentRecord paymentRecord;
    private final Instant createdAt;

    public SaleOrder(OrderId id, CPF customerCpf) {
        if (id == null) {
            throw new DomainValidationException("OrderId cannot be null");
        }
        this.id = id;
        this.customerCpf = customerCpf;
        this.status = OrderStatus.DRAFT;
        this.discountStrategy = order -> Money.zero();
        this.createdAt = Instant.now();
    }

    public static SaleOrder create(OrderId id) {
        return new SaleOrder(id, null);
    }

    public static SaleOrder create(OrderId id, CPF customerCpf) {
        return new SaleOrder(id, customerCpf);
    }

    public OrderId getId() {
        return id;
    }

    public Optional<CPF> getCustomerCpf() {
        return Optional.ofNullable(customerCpf);
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Collection<OrderItem> getItems() {
        return Collections.unmodifiableCollection(items.values());
    }

    public int getTotalItemCount() {
        return items.values().stream().mapToInt(OrderItem::getQuantity).sum();
    }

    public Optional<PaymentRecord> getPaymentRecord() {
        return Optional.ofNullable(paymentRecord);
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void addItem(SKU sku, String name, Money unitPrice, int quantity) {
        assertDraftStatus("Cannot add items to order that is " + status);
        if (items.containsKey(sku)) {
            items.get(sku).addQuantity(quantity);
        } else {
            items.put(sku, new OrderItem(sku, name, unitPrice, quantity));
        }
    }

    public void removeItem(SKU sku) {
        assertDraftStatus("Cannot remove items from order that is " + status);
        if (!items.containsKey(sku)) {
            throw new DomainValidationException("Item not found in order: " + sku);
        }
        items.remove(sku);
    }

    public void applyDiscountStrategy(DiscountStrategy strategy) {
        assertDraftStatus("Cannot apply discount to order that is " + status);
        this.discountStrategy = Objects.requireNonNullElseGet(strategy, () -> order -> Money.zero());
    }

    public Money calculateSubtotal() {
        Money total = Money.zero();
        for (OrderItem item : items.values()) {
            total = total.add(item.calculateSubtotal());
        }
        return total;
    }

    public Money calculateDiscount() {
        if (discountStrategy == null || items.isEmpty()) {
            return Money.zero();
        }
        Money rawDiscount = discountStrategy.calculateDiscount(this);
        if (rawDiscount.isNegative()) {
            throw new DomainValidationException("Discount cannot be negative");
        }
        Money subtotal = calculateSubtotal();
        // Cap discount at subtotal (cannot make order negative)
        return rawDiscount.isGreaterThan(subtotal) ? subtotal : rawDiscount;
    }

    public Money calculateTotal() {
        return calculateSubtotal().subtract(calculateDiscount());
    }

    public void confirm() {
        assertDraftStatus("Only DRAFT orders can be confirmed, current status: " + status);
        if (items.isEmpty()) {
            throw new DomainValidationException("Cannot confirm an order with zero items");
        }
        this.status = OrderStatus.CONFIRMED;
    }

    public void pay(PaymentMethod method, Money amountPaid) {
        if (status != OrderStatus.CONFIRMED) {
            throw new DomainValidationException("Order must be CONFIRMED before payment. Current status: " + status);
        }
        Money expectedTotal = calculateTotal();
        if (amountPaid.isLessThan(expectedTotal)) {
            throw new DomainValidationException(String.format(
                    "Insufficient payment amount. Total due: %s, Paid: %s",
                    expectedTotal.formatted(), amountPaid.formatted()
            ));
        }
        this.paymentRecord = new PaymentRecord(method, amountPaid, Instant.now());
        this.status = OrderStatus.PAID;
    }

    public void cancel() {
        if (status == OrderStatus.CANCELLED) {
            throw new DomainValidationException("Order is already cancelled");
        }
        this.status = OrderStatus.CANCELLED;
    }

    private void assertDraftStatus(String message) {
        if (status != OrderStatus.DRAFT) {
            throw new DomainValidationException(message);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SaleOrder saleOrder)) return false;
        return Objects.equals(id, saleOrder.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Order [%s] Status: %s Total: %s", id.getValue(), status, calculateTotal().formatted());
    }
}
