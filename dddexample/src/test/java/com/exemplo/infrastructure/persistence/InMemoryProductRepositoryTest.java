package com.exemplo.infrastructure.persistence;

import com.exemplo.domain.catalog.model.*;
import com.exemplo.domain.shared.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryProductRepositoryTest {

    private InMemoryProductRepository repository;
    private Product shirt;
    private Product pants;

    @BeforeEach
    void setUp() {
        repository = new InMemoryProductRepository();

        shirt = new Product(ProductId.of("SHIRT-1"), "Polo Classic", "Lacoste", ClothingCategory.SHIRTS, Money.of(350.0));
        shirt.addVariant(ClothingSize.M, Color.BLUE, null, 10, 2);
        shirt.addVariant(ClothingSize.G, Color.WHITE, null, 1, 3); // Low stock!

        pants = new Product(ProductId.of("PANTS-1"), "Jeans Slim", "Levis", ClothingCategory.PANTS, Money.of(299.90));
        pants.addVariant(ClothingSize.NUM_40, Color.BLACK, null, 15, 5);

        repository.save(shirt);
        repository.save(pants);
    }

    @Test
    @DisplayName("Should find product by ProductId")
    void shouldFindById() {
        Optional<Product> found = repository.findById(ProductId.of("SHIRT-1"));
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Polo Classic");
    }

    @Test
    @DisplayName("Should find product by variant SKU")
    void shouldFindBySku() {
        SKU sku = shirt.getVariants().iterator().next().getSku();
        Optional<Product> found = repository.findBySku(sku);

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(shirt.getId());
    }

    @Test
    @DisplayName("Should find all products")
    void shouldFindAll() {
        List<Product> all = repository.findAll();
        assertThat(all).hasSize(2);
    }

    @Test
    @DisplayName("Should filter products by category")
    void shouldFindByCategory() {
        List<Product> shirts = repository.findByCategory(ClothingCategory.SHIRTS);
        assertThat(shirts).containsExactly(shirt);

        List<Product> pantsList = repository.findByCategory(ClothingCategory.PANTS);
        assertThat(pantsList).containsExactly(pants);
    }

    @Test
    @DisplayName("Should find products with low stock variants")
    void shouldFindLowStockProducts() {
        List<Product> lowStock = repository.findLowStockProducts();
        assertThat(lowStock).containsExactly(shirt);
    }

    @Test
    @DisplayName("Should filter products using Specification pattern")
    void shouldFindBySpecification() {
        com.exemplo.domain.catalog.specification.ProductSpecification spec =
                new com.exemplo.domain.catalog.specification.ByCategorySpecification(ClothingCategory.SHIRTS)
                        .and(new com.exemplo.domain.catalog.specification.InStockSpecification());

        List<Product> matches = repository.findBySpecification(spec);
        assertThat(matches).containsExactly(shirt);
    }

    @Test
    @DisplayName("Should check existence and delete product")
    void shouldDeleteProduct() {
        assertThat(repository.existsById(ProductId.of("SHIRT-1"))).isTrue();

        repository.delete(ProductId.of("SHIRT-1"));

        assertThat(repository.existsById(ProductId.of("SHIRT-1"))).isFalse();
        assertThat(repository.findById(ProductId.of("SHIRT-1"))).isEmpty();
    }
}
