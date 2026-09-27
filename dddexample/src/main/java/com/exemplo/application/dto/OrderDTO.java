package com.exemplo.application.dto;

import java.math.BigDecimal;
import java.util.List;

public record OrderDTO(
        String orderId,
        String customerCpf,
        String status,
        int totalItems,
        BigDecimal subtotal,
        String formattedSubtotal,
        BigDecimal discount,
        String formattedDiscount,
        BigDecimal total,
        String formattedTotal,
        String paymentMethod,
        String paidAt,
        List<OrderItemDTO> items
) {}
