package com.exemplo;

import com.exemplo.application.catalog.CatalogService;
import com.exemplo.application.sales.SalesService;
import com.exemplo.domain.catalog.repository.ProductRepository;
import com.exemplo.domain.sales.repository.SaleOrderRepository;
import com.exemplo.infrastructure.persistence.InMemoryProductRepository;
import com.exemplo.infrastructure.persistence.InMemorySaleOrderRepository;
import com.exemplo.infrastructure.ui.ConsoleApp;
import com.exemplo.infrastructure.ui.DataSeeder;

/**
 * Composition Root of the Clothing Store Management System.
 * Instantiates domain repositories, application services, seeds initial data, and boots the CLI.
 */
public class Main {
    public static void main(String[] args) {
        // Infrastructure: In-Memory Repositories
        ProductRepository productRepository = new InMemoryProductRepository();
        SaleOrderRepository orderRepository = new InMemorySaleOrderRepository();

        // Application Services
        CatalogService catalogService = new CatalogService(productRepository);
        SalesService salesService = new SalesService(orderRepository, productRepository);

        // Seed Initial Inventory
        DataSeeder.seed(catalogService);

        // UI Presentation Layer
        ConsoleApp app = new ConsoleApp(catalogService, salesService);
        app.start();
    }
}