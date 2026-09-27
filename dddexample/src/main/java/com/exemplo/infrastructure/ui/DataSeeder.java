package com.exemplo.infrastructure.ui;

import com.exemplo.application.catalog.CatalogService;
import com.exemplo.application.dto.ProductDTO;
import com.exemplo.domain.catalog.model.ClothingCategory;
import com.exemplo.domain.catalog.model.ClothingSize;
import com.exemplo.domain.catalog.model.Color;
import com.exemplo.domain.shared.Money;

/**
 * Seeds initial realistic clothing items and inventory for demonstration.
 */
public class DataSeeder {

    public static void seed(CatalogService catalog) {
        // 1. Polo Shirt
        ProductDTO polo = catalog.registerProduct("Camisa Polo Piquet", "Dudalina", ClothingCategory.SHIRTS, Money.of(189.90));
        catalog.addVariant(polo.id(), ClothingSize.M, Color.NAVY, "7891000000010", 10, 3);
        catalog.addVariant(polo.id(), ClothingSize.G, Color.WHITE, "7891000000020", 2, 3); // Low stock!
        catalog.addVariant(polo.id(), ClothingSize.GG, Color.BLACK, "7891000000030", 6, 2);

        // 2. Jeans
        ProductDTO jeans = catalog.registerProduct("Calça Jeans Slim", "Levis", ClothingCategory.PANTS, Money.of(289.00));
        catalog.addVariant(jeans.id(), ClothingSize.NUM_38, Color.BLUE, "7892000000010", 8, 2);
        catalog.addVariant(jeans.id(), ClothingSize.NUM_40, Color.BLACK, "7892000000020", 1, 3); // Low stock!
        catalog.addVariant(jeans.id(), ClothingSize.NUM_42, Color.BLUE, "7892000000030", 5, 2);

        // 3. Dress
        ProductDTO dress = catalog.registerProduct("Vestido Midi Tropical", "Farm", ClothingCategory.DRESSES, Money.of(349.90));
        catalog.addVariant(dress.id(), ClothingSize.P, Color.GREEN, "7893000000010", 5, 2);
        catalog.addVariant(dress.id(), ClothingSize.M, Color.RED, "7893000000020", 7, 2);

        // 4. Jacket
        ProductDTO jacket = catalog.registerProduct("Jaqueta Corta-Vento", "Nike", ClothingCategory.JACKETS, Money.of(259.00));
        catalog.addVariant(jacket.id(), ClothingSize.M, Color.BLACK, "7894000000010", 4, 2);
        catalog.addVariant(jacket.id(), ClothingSize.G, Color.GRAY, "7894000000020", 2, 2);
    }
}
