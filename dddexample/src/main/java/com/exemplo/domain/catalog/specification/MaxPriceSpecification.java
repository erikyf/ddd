package com.exemplo.domain.catalog.specification;

import com.exemplo.domain.catalog.model.Product;
import com.exemplo.domain.shared.Money;

import java.util.Objects;

/**
 * Specification that filters products with base price less than or equal to a threshold.
 */
public class MaxPriceSpecification implements ProductSpecification {

    private final Money maxPrice;

    public MaxPriceSpecification(Money maxPrice) {
        this.maxPrice = Objects.requireNonNull(maxPrice, "Max price cannot be null");
    }

    @Override
    public boolean isSatisfiedBy(Product product) {
        return product != null && product.getBasePrice().isLessThanOrEqual(maxPrice);
    }
}
