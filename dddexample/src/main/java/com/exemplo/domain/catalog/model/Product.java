package com.exemplo.domain.catalog.model;

import com.exemplo.domain.shared.DomainValidationException;
import com.exemplo.domain.shared.Money;

import java.util.*;

/**
 * Aggregate Root for the Product entity in the Catalog bounded context.
 * Enforces business invariants across all clothing variants and pricing.
 */
public class Product {

    private final ProductId id;
    private String name;
    private String brand;
    private ClothingCategory category;
    private Money basePrice;
    private final Map<SKU, ProductVariant> variants = new LinkedHashMap<>();

    public Product(ProductId id, String name, String brand, ClothingCategory category, Money basePrice) {
        if (id == null) {
            throw new DomainValidationException("ProductId cannot be null");
        }
        validateNameAndBrand(name, brand);
        if (category == null) {
            throw new DomainValidationException("ClothingCategory cannot be null");
        }
        validateBasePrice(basePrice);

        this.id = id;
        this.name = name.trim();
        this.brand = brand.trim();
        this.category = category;
        this.basePrice = basePrice;
    }

    private void validateNameAndBrand(String name, String brand) {
        if (name == null || name.trim().isEmpty()) {
            throw new DomainValidationException("Product name cannot be blank");
        }
        if (brand == null || brand.trim().isEmpty()) {
            throw new DomainValidationException("Product brand cannot be blank");
        }
    }

    private void validateBasePrice(Money price) {
        if (price == null) {
            throw new DomainValidationException("Base price cannot be null");
        }
        if (!price.isPositive()) {
            throw new DomainValidationException("Base price must be greater than zero, but was: " + price);
        }
    }

    public ProductId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBrand() {
        return brand;
    }

    public ClothingCategory getCategory() {
        return category;
    }

    public Money getBasePrice() {
        return basePrice;
    }

    public Collection<ProductVariant> getVariants() {
        return Collections.unmodifiableCollection(variants.values());
    }

    public void updateBasePrice(Money newPrice) {
        validateBasePrice(newPrice);
        this.basePrice = newPrice;
    }

    public void updateDetails(String name, String brand, ClothingCategory category) {
        validateNameAndBrand(name, brand);
        if (category == null) {
            throw new DomainValidationException("ClothingCategory cannot be null");
        }
        this.name = name.trim();
        this.brand = brand.trim();
        this.category = category;
    }

    /**
     * Adds a new variant to the product aggregate, ensuring uniqueness of SKU and Size+Color matrix.
     */
    public ProductVariant addVariant(ClothingSize size, Color color, Barcode barcode, int initialStock, int minStockThreshold) {
        // Enforce invariant: Size + Color combination must be unique within the product
        for (ProductVariant existing : variants.values()) {
            if (existing.getSize().equals(size) && existing.getColor().equals(color)) {
                throw new DomainValidationException(String.format(
                        "Variant with size %s and color %s already exists for product %s",
                        size.getCode(), color.getName(), name
                ));
            }
        }

        SKU generatedSku = SKU.generate(brand, name, color, size);
        ProductVariant variant = new ProductVariant(generatedSku, size, color, barcode, initialStock, minStockThreshold);
        this.variants.put(generatedSku, variant);
        return variant;
    }

    public void addVariant(ProductVariant variant) {
        if (variant == null) {
            throw new DomainValidationException("Variant cannot be null");
        }
        if (variants.containsKey(variant.getSku())) {
            throw new DomainValidationException("Variant with SKU already exists: " + variant.getSku());
        }
        for (ProductVariant existing : variants.values()) {
            if (existing.getSize().equals(variant.getSize()) && existing.getColor().equals(variant.getColor())) {
                throw new DomainValidationException(String.format(
                        "Variant with size %s and color %s already exists for product %s",
                        variant.getSize().getCode(), variant.getColor().getName(), name
                ));
            }
        }
        this.variants.put(variant.getSku(), variant);
    }

    public ProductVariant getVariant(SKU sku) {
        ProductVariant variant = variants.get(sku);
        if (variant == null) {
            throw new DomainValidationException(String.format(
                    "SKU %s not found in product %s (ID: %s)",
                    sku.getValue(), name, id.getValue()
            ));
        }
        return variant;
    }

    public boolean hasVariant(SKU sku) {
        return variants.containsKey(sku);
    }

    public void replenishStock(SKU sku, int quantity) {
        ProductVariant variant = getVariant(sku);
        variant.addStock(quantity);
    }

    public void deductStock(SKU sku, int quantity) {
        ProductVariant variant = getVariant(sku);
        variant.deductStock(quantity);
    }

    public int getTotalStock() {
        return variants.values().stream().mapToInt(ProductVariant::getStockQuantity).sum();
    }

    public List<ProductVariant> getLowStockVariants() {
        return variants.values().stream().filter(ProductVariant::isLowStock).toList();
    }

    public boolean hasLowStock() {
        return variants.values().stream().anyMatch(ProductVariant::isLowStock);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product product)) return false;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("%s - %s (%s) [%s]", brand, name, category, id.getValue());
    }
}
