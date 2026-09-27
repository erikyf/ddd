package com.exemplo.application.catalog;

import com.exemplo.application.dto.ProductDTO;
import com.exemplo.application.dto.VariantDTO;
import com.exemplo.domain.catalog.model.ClothingCategory;
import com.exemplo.domain.catalog.model.ClothingSize;
import com.exemplo.domain.catalog.model.Color;
import com.exemplo.domain.shared.Money;
import com.exemplo.infrastructure.persistence.InMemoryProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CatalogServiceTest {

    private CatalogService catalogService;
    private InMemoryProductRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryProductRepository();
        catalogService = new CatalogService(repository);
    }

    @Test
    @DisplayName("Should register product and add variants via use cases")
    void shouldRegisterProductAndVariants() {
        ProductDTO product = catalogService.registerProduct("Vestido Floral", "Farm", ClothingCategory.DRESSES, Money.of(399.0));
        assertThat(product.id()).isNotBlank();
        assertThat(product.name()).isEqualTo("Vestido Floral");

        VariantDTO variant = catalogService.addVariant(product.id(), ClothingSize.M, Color.RED, "7890011223344", 8, 2);
        assertThat(variant.sku()).isNotBlank();
        assertThat(variant.stockQuantity()).isEqualTo(8);

        List<ProductDTO> all = catalogService.listAllProducts();
        assertThat(all).hasSize(1);
        assertThat(all.get(0).totalStock()).isEqualTo(8);
    }

    @Test
    @DisplayName("Should replenish stock for SKU")
    void shouldReplenishStock() {
        ProductDTO product = catalogService.registerProduct("Camisa Social", "Aramis", ClothingCategory.SHIRTS, Money.of(280.0));
        VariantDTO variant = catalogService.addVariant(product.id(), ClothingSize.G, Color.WHITE, null, 5, 2);

        catalogService.replenishStock(variant.sku(), 10);

        ProductDTO updated = catalogService.findById(product.id()).orElseThrow();
        assertThat(updated.totalStock()).isEqualTo(15);
    }

    @Test
    @DisplayName("Should detect low stock variants across catalog")
    void shouldDetectLowStock() {
        ProductDTO product = catalogService.registerProduct("Calça Alfaiataria", "Zara", ClothingCategory.PANTS, Money.of(220.0));
        VariantDTO variant = catalogService.addVariant(product.id(), ClothingSize.NUM_42, Color.BLACK, null, 1, 3); // Low stock!

        List<VariantDTO> lowStockList = catalogService.listLowStockVariants();
        assertThat(lowStockList).hasSize(1);
        assertThat(lowStockList.get(0).sku()).isEqualTo(variant.sku());
    }
}
