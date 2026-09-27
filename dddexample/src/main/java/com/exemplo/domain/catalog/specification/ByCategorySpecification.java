package com.exemplo.domain.catalog.specification;

import com.exemplo.domain.catalog.model.ClothingCategory;
import com.exemplo.domain.catalog.model.Product;

import java.util.Objects;

/**
 * Specification that filters products by clothing category.
 */
public class ByCategorySpecification implements ProductSpecification {

    private final ClothingCategory category;

    public ByCategorySpecification(ClothingCategory category) {
        this.category = Objects.requireNonNull(category, "Category cannot be null");
    }

    @Override
    public boolean isSatisfiedBy(Product product) {
        return product != null && product.getCategory() == category;
    }
}
