package com.exemplo.domain.catalog.specification;

import com.exemplo.domain.catalog.model.Product;

/**
 * Specification Pattern interface for evaluating business criteria on Products.
 */
@FunctionalInterface
public interface ProductSpecification {

    boolean isSatisfiedBy(Product product);

    default ProductSpecification and(ProductSpecification other) {
        return product -> this.isSatisfiedBy(product) && other.isSatisfiedBy(product);
    }

    default ProductSpecification or(ProductSpecification other) {
        return product -> this.isSatisfiedBy(product) || other.isSatisfiedBy(product);
    }

    default ProductSpecification not() {
        return product -> !this.isSatisfiedBy(product);
    }
}
