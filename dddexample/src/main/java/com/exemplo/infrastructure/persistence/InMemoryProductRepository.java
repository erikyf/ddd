package com.exemplo.infrastructure.persistence;

import com.exemplo.domain.catalog.model.ClothingCategory;
import com.exemplo.domain.catalog.model.Product;
import com.exemplo.domain.catalog.model.ProductId;
import com.exemplo.domain.catalog.model.SKU;
import com.exemplo.domain.catalog.repository.ProductRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-Memory thread-safe implementation of ProductRepository.
 * Useful for development, unit testing, and lightweight CLI execution without external DB dependencies.
 */
public class InMemoryProductRepository implements ProductRepository {

    private final Map<ProductId, Product> storage = new ConcurrentHashMap<>();

    @Override
    public void save(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Cannot save null product");
        }
        storage.put(product.getId(), product);
    }

    @Override
    public Optional<Product> findById(ProductId id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Optional<Product> findBySku(SKU sku) {
        if (sku == null) return Optional.empty();
        return storage.values().stream()
                .filter(product -> product.hasVariant(sku))
                .findFirst();
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public List<Product> findByCategory(ClothingCategory category) {
        if (category == null) return Collections.emptyList();
        return storage.values().stream()
                .filter(p -> p.getCategory() == category)
                .toList();
    }

    @Override
    public List<Product> findBySpecification(com.exemplo.domain.catalog.specification.ProductSpecification specification) {
        if (specification == null) return Collections.emptyList();
        return storage.values().stream()
                .filter(specification::isSatisfiedBy)
                .toList();
    }

    @Override
    public List<Product> findLowStockProducts() {
        return storage.values().stream()
                .filter(Product::hasLowStock)
                .toList();
    }

    @Override
    public boolean existsById(ProductId id) {
        return id != null && storage.containsKey(id);
    }

    @Override
    public void delete(ProductId id) {
        if (id != null) {
            storage.remove(id);
        }
    }

    public void clear() {
        storage.clear();
    }
}
