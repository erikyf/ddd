package com.exemplo.domain.sales.model;

/**
 * Supported payment methods in the retail store.
 */
public enum PaymentMethod {
    CASH("Dinheiro"),
    PIX("Pix"),
    CREDIT_CARD("Cartão de Crédito"),
    DEBIT_CARD("Cartão de Débito");

    private final String description;

    PaymentMethod(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
