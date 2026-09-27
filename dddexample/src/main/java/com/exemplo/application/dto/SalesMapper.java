package com.exemplo.application.dto;

import com.exemplo.domain.sales.model.OrderItem;
import com.exemplo.domain.sales.model.SaleOrder;

import java.util.stream.Collectors;

public final class SalesMapper {

    private SalesMapper() {}

    public static OrderDTO toDTO(SaleOrder order) {
        return new OrderDTO(
                order.getId().getValue(),
                order.getCustomerCpf().map(cpf -> cpf.formatted()).orElse("Não Informado"),
                order.getStatus().getDescription(),
                order.getTotalItemCount(),
                order.calculateSubtotal().getAmount(),
                order.calculateSubtotal().formatted(),
                order.calculateDiscount().getAmount(),
                order.calculateDiscount().formatted(),
                order.calculateTotal().getAmount(),
                order.calculateTotal().formatted(),
                order.getPaymentRecord().map(p -> p.getMethod().getDescription()).orElse("Pendente"),
                order.getPaymentRecord().map(p -> p.getPaidAt().toString()).orElse(null),
                order.getItems().stream().map(SalesMapper::toDTO).collect(Collectors.toList())
        );
    }

    public static OrderItemDTO toDTO(OrderItem item) {
        return new OrderItemDTO(
                item.getSku().getValue(),
                item.getProductName(),
                item.getUnitPrice().getAmount(),
                item.getUnitPrice().formatted(),
                item.getQuantity(),
                item.calculateSubtotal().getAmount(),
                item.calculateSubtotal().formatted()
        );
    }
}
