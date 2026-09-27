package com.exemplo.domain.catalog.model;

import com.exemplo.domain.shared.DomainValidationException;

/**
 * Value Object representing clothing sizes (Letter sizes and Brazilian numeric garment sizes).
 */
public enum ClothingSize {
    PP("PP", "Extra Pequeno"),
    P("P", "Pequeno"),
    M("M", "Médio"),
    G("G", "Grande"),
    GG("GG", "Extra Grande"),
    XG("XG", "Extra Extra Grande"),
    UNICO("UN", "Tamanho Único"),
    NUM_34("34", "Tamanho 34"),
    NUM_36("36", "Tamanho 36"),
    NUM_38("38", "Tamanho 38"),
    NUM_40("40", "Tamanho 40"),
    NUM_42("42", "Tamanho 42"),
    NUM_44("44", "Tamanho 44"),
    NUM_46("46", "Tamanho 46"),
    NUM_48("48", "Tamanho 48"),
    NUM_50("50", "Tamanho 50"),
    NUM_52("52", "Tamanho 52");

    private final String code;
    private final String label;

    ClothingSize(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public static ClothingSize fromString(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            throw new DomainValidationException("Clothing size cannot be blank");
        }
        String normalized = raw.trim().toUpperCase();
        for (ClothingSize size : values()) {
            if (size.code.equalsIgnoreCase(normalized) || size.name().equalsIgnoreCase(normalized)) {
                return size;
            }
        }
        throw new DomainValidationException(
                "Invalid clothing size: '" + raw + "'. Supported sizes: PP, P, M, G, GG, XG, UN, 34..52"
        );
    }

    @Override
    public String toString() {
        return code;
    }
}
