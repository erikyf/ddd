package com.exemplo.domain.catalog.repository;

import com.exemplo.domain.catalog.model.ClothingCategory;
import com.exemplo.domain.catalog.model.Product;
import com.exemplo.domain.catalog.model.ProductId;
import com.exemplo.domain.catalog.model.SKU;

import java.util.List;
import java.util.Optional;

/**
 * Domain Repository interface for managing Product aggregate persistence.
 * Follows the DDD Repository pattern to encapsulate aggregate lifecycle operations.
 */
public interface ProductRepository {

    void save(Product product);

    Optional<Product> findById(ProductId id);

    Optional<Product> findBySku(SKU sku);

    List<Product> findAll();

    List<Product> findByCategory(ClothingCategory category);

    List<Product> findBySpecification(com.exemplo.domain.catalog.specification.ProductSpecification specification);

    List<Product> findLowStockProducts();

    boolean existsById(ProductId id);

    void delete(ProductId id);
}
