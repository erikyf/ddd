package com.exemplo.application.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProductDTO(
        String id,
        String name,
        String brand,
        String category,
        String categoryDescription,
        BigDecimal basePrice,
        String formattedPrice,
        int totalStock,
        boolean hasLowStock,
        List<VariantDTO> variants
) {}
