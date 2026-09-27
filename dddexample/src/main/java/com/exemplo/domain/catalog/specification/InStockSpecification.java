package com.exemplo.domain.catalog.specification;

import com.exemplo.domain.catalog.model.Product;

/**
 * Specification that checks if a product has at least one variant in stock.
 */
public class InStockSpecification implements ProductSpecification {

    @Override
    public boolean isSatisfiedBy(Product product) {
        return product != null && product.getTotalStock() > 0;
    }
}
