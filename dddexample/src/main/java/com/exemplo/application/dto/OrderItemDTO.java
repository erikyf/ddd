package com.exemplo.application.dto;

import java.math.BigDecimal;

public record OrderItemDTO(
        String sku,
        String productName,
        BigDecimal unitPrice,
        String formattedUnitPrice,
        int quantity,
        BigDecimal subtotal,
        String formattedSubtotal
) {}
