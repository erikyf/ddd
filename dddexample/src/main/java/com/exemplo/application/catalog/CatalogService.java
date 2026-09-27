package com.exemplo.application.catalog;

import com.exemplo.application.dto.CatalogMapper;
import com.exemplo.application.dto.ProductDTO;
import com.exemplo.application.dto.VariantDTO;
import com.exemplo.domain.catalog.model.*;
import com.exemplo.domain.catalog.repository.ProductRepository;
import com.exemplo.domain.shared.DomainValidationException;
import com.exemplo.domain.shared.Money;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Application service orchestrating Catalog and Inventory use cases.
 */
public class CatalogService {

    private final ProductRepository productRepository;

    public CatalogService(ProductRepository productRepository) {
        this.productRepository = Objects.requireNonNull(productRepository, "ProductRepository cannot be null");
    }

    public ProductDTO registerProduct(String name, String brand, ClothingCategory category, Money basePrice) {
        ProductId id = ProductId.generate();
        Product product = new Product(id, name, brand, category, basePrice);
        productRepository.save(product);
        return CatalogMapper.toDTO(product);
    }

    public VariantDTO addVariant(String productId, ClothingSize size, Color color, String rawBarcode, int initialStock, int minStockThreshold) {
        Product product = productRepository.findById(ProductId.of(productId))
                .orElseThrow(() -> new DomainValidationException("Product not found with ID: " + productId));

        Barcode barcode = (rawBarcode != null && !rawBarcode.trim().isEmpty()) ? Barcode.of(rawBarcode) : null;
        ProductVariant variant = product.addVariant(size, color, barcode, initialStock, minStockThreshold);
        productRepository.save(product);

        return CatalogMapper.toDTO(variant);
    }

    public void replenishStock(String rawSku, int quantity) {
        SKU sku = SKU.of(rawSku);
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new DomainValidationException("Product variant not found with SKU: " + rawSku));

        product.replenishStock(sku, quantity);
        productRepository.save(product);
    }

    public List<ProductDTO> listAllProducts() {
        return productRepository.findAll().stream()
                .map(CatalogMapper::toDTO)
                .toList();
    }

    public List<ProductDTO> listByCategory(ClothingCategory category) {
        return productRepository.findByCategory(category).stream()
                .map(CatalogMapper::toDTO)
                .toList();
    }

    public List<VariantDTO> listLowStockVariants() {
        List<VariantDTO> lowStock = new ArrayList<>();
        for (Product product : productRepository.findLowStockProducts()) {
            for (ProductVariant variant : product.getLowStockVariants()) {
                lowStock.add(CatalogMapper.toDTO(variant));
            }
        }
        return lowStock;
    }

    public Optional<ProductDTO> findById(String productId) {
        return productRepository.findById(ProductId.of(productId)).map(CatalogMapper::toDTO);
    }

    public Optional<ProductDTO> findBySku(String rawSku) {
        return productRepository.findBySku(SKU.of(rawSku)).map(CatalogMapper::toDTO);
    }
}
