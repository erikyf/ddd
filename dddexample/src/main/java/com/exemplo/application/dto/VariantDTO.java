package com.exemplo.application.dto;

public record VariantDTO(
        String sku,
        String size,
        String color,
        String barcode,
        int stockQuantity,
        int minStockThreshold,
        boolean lowStock
) {}
