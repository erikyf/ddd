package com.exemplo.domain.sales.model;

/**
 * Lifecycle states of a SaleOrder in the sales bounded context.
 */
public enum OrderStatus {
    DRAFT("Em Aberto"),
    CONFIRMED("Confirmado"),
    PAID("Pago"),
    CANCELLED("Cancelado");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
