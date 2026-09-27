package com.exemplo.domain.catalog.specification;

import com.exemplo.domain.catalog.model.ClothingCategory;
import com.exemplo.domain.catalog.model.ClothingSize;
import com.exemplo.domain.catalog.model.Color;
import com.exemplo.domain.catalog.model.Product;
import com.exemplo.domain.catalog.model.ProductId;
import com.exemplo.domain.shared.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductSpecificationTest {

    private Product shirtInStock;
    private Product outOfStockPants;

    @BeforeEach
    void setUp() {
        shirtInStock = new Product(ProductId.of("1"), "Camisa Polo", "BrandA", ClothingCategory.SHIRTS, Money.of(120.0));
        shirtInStock.addVariant(ClothingSize.M, Color.BLUE, null, 5, 2);

        outOfStockPants = new Product(ProductId.of("2"), "Calca Jeans", "BrandB", ClothingCategory.PANTS, Money.of(250.0));
        outOfStockPants.addVariant(ClothingSize.NUM_38, Color.BLACK, null, 0, 1);
    }

    @Test
    @DisplayName("InStockSpecification should identify products with stock")
    void testInStockSpecification() {
        InStockSpecification inStock = new InStockSpecification();

        assertThat(inStock.isSatisfiedBy(shirtInStock)).isTrue();
        assertThat(inStock.isSatisfiedBy(outOfStockPants)).isFalse();
    }

    @Test
    @DisplayName("ByCategorySpecification should filter by category")
    void testByCategorySpecification() {
        ByCategorySpecification isShirt = new ByCategorySpecification(ClothingCategory.SHIRTS);

        assertThat(isShirt.isSatisfiedBy(shirtInStock)).isTrue();
        assertThat(isShirt.isSatisfiedBy(outOfStockPants)).isFalse();
    }

    @Test
    @DisplayName("MaxPriceSpecification should filter by price threshold")
    void testMaxPriceSpecification() {
        MaxPriceSpecification under200 = new MaxPriceSpecification(Money.of(200.0));

        assertThat(under200.isSatisfiedBy(shirtInStock)).isTrue();
        assertThat(under200.isSatisfiedBy(outOfStockPants)).isFalse();
    }

    @Test
    @DisplayName("Combinators (and, or, not) should compose specifications correctly")
    void testCombinators() {
        ProductSpecification inStockShirtsUnder200 = new InStockSpecification()
                .and(new ByCategorySpecification(ClothingCategory.SHIRTS))
                .and(new MaxPriceSpecification(Money.of(150.0)));

        assertThat(inStockShirtsUnder200.isSatisfiedBy(shirtInStock)).isTrue();
        assertThat(inStockShirtsUnder200.isSatisfiedBy(outOfStockPants)).isFalse();

        ProductSpecification outOfStock = new InStockSpecification().not();
        assertThat(outOfStock.isSatisfiedBy(outOfStockPants)).isTrue();
    }
}
