package com.exemplo.domain.catalog.model;

/**
 * Categories of clothing items supported in the store catalog.
 */
public enum ClothingCategory {
    SHIRTS("Camisas / Camisetas"),
    PANTS("Calças / Jeans"),
    SHORTS("Bermudas / Shorts"),
    DRESSES("Vestidos"),
    SKIRTS("Saias"),
    JACKETS("Casacos / Jaquetas"),
    UNDERWEAR("Moda Íntima"),
    BEACHWEAR("Moda Praia"),
    FOOTWEAR("Calçados"),
    ACCESSORIES("Acessórios");

    private final String description;

    ClothingCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
