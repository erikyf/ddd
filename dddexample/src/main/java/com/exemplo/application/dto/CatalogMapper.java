package com.exemplo.application.dto;

import com.exemplo.domain.catalog.model.Product;
import com.exemplo.domain.catalog.model.ProductVariant;
import com.exemplo.domain.sales.model.OrderItem;
import com.exemplo.domain.sales.model.SaleOrder;

import java.util.stream.Collectors;

public final class CatalogMapper {

    private CatalogMapper() {}

    public static ProductDTO toDTO(Product product) {
        return new ProductDTO(
                product.getId().getValue(),
                product.getName(),
                product.getBrand(),
                product.getCategory().name(),
                product.getCategory().getDescription(),
                product.getBasePrice().getAmount(),
                product.getBasePrice().formatted(),
                product.getTotalStock(),
                product.hasLowStock(),
                product.getVariants().stream().map(CatalogMapper::toDTO).collect(Collectors.toList())
        );
    }

    public static VariantDTO toDTO(ProductVariant variant) {
        return new VariantDTO(
                variant.getSku().getValue(),
                variant.getSize().getCode(),
                variant.getColor().getName(),
                variant.getBarcode() != null ? variant.getBarcode().getValue() : null,
                variant.getStockQuantity(),
                variant.getMinStockThreshold(),
                variant.isLowStock()
        );
    }
}
