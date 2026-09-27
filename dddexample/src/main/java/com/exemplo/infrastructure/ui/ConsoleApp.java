package com.exemplo.infrastructure.ui;

import com.exemplo.application.catalog.CatalogService;
import com.exemplo.application.dto.OrderDTO;
import com.exemplo.application.dto.OrderItemDTO;
import com.exemplo.application.dto.ProductDTO;
import com.exemplo.application.dto.VariantDTO;
import com.exemplo.application.sales.SalesService;
import com.exemplo.domain.catalog.model.ClothingCategory;
import com.exemplo.domain.catalog.model.ClothingSize;
import com.exemplo.domain.catalog.model.Color;
import com.exemplo.domain.sales.model.PaymentMethod;
import com.exemplo.domain.sales.policy.NoDiscountStrategy;
import com.exemplo.domain.sales.policy.PercentageDiscountStrategy;
import com.exemplo.domain.sales.policy.VolumeDiscountStrategy;
import com.exemplo.domain.shared.DomainValidationException;
import com.exemplo.domain.shared.Money;

import java.util.List;
import java.util.Scanner;

/**
 * Interactive Console CLI for the Clothing Store Management System.
 */
public class ConsoleApp {

    private final CatalogService catalogService;
    private final SalesService salesService;
    private final Scanner scanner;

    public ConsoleApp(CatalogService catalogService, SalesService salesService) {
        this.catalogService = catalogService;
        this.salesService = salesService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("======================================================");
        System.out.println("   SISTEMA DE GESTÃO DE LOJA DE ROUPAS (DDD & CLEAN)  ");
        System.out.println("======================================================");

        boolean running = true;
        while (running) {
            printMainMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> listCatalog();
                case "2" -> registerProduct();
                case "3" -> replenishStock();
                case "4" -> showLowStockAlerts();
                case "5" -> pointOfSale();
                case "6" -> listOrders();
                case "7" -> showFinancialSummary();
                case "8" -> reloadSeedData();
                case "0" -> {
                    System.out.println("\nFinalizando o sistema... Até logo!");
                    running = false;
                }
                default -> System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

    private void printMainMenu() {
        System.out.println("\n---------------- MENU PRINCIPAL ----------------");
        System.out.println("1. 👗 Catálogo de Produtos e Variantes");
        System.out.println("2. ➕ Cadastrar Novo Produto e Variante");
        System.out.println("3. 📦 Repor Estoque de Mercadorias");
        System.out.println("4. ⚠️ Alertas de Estoque Baixo");
        System.out.println("5. 🛒 Nova Venda / Caixa (PDV)");
        System.out.println("6. 🧾 Listar Pedidos e Histórico de Vendas");
        System.out.println("7. 💰 Relatório Financeiro / Fechamento de Caixa");
        System.out.println("8. 🧪 Recarregar Dados de Demonstração");
        System.out.println("0. 🚪 Sair");
        System.out.print("Escolha uma opção: ");
    }

    private void listCatalog() {
        System.out.println("\n================ CATÁLOGO DE ROUPAS ================");
        List<ProductDTO> products = catalogService.listAllProducts();
        if (products.isEmpty()) {
            System.out.println("Nenhum produto cadastrado no momento.");
            return;
        }

        for (ProductDTO p : products) {
            System.out.printf("\n🏷️  [%s] %s %s (%s) - Preço Base: %s | Estoque Total: %d\n",
                    p.id(), p.brand(), p.name(), p.categoryDescription(), p.formattedPrice(), p.totalStock());
            if (p.variants().isEmpty()) {
                System.out.println("   (Nenhuma variante cadastrada)");
            } else {
                System.out.println("   Variantes:");
                for (VariantDTO v : p.variants()) {
                    String alert = v.lowStock() ? " ⚠️ [ESTOQUE BAIXO!]" : "";
                    System.out.printf("   - SKU: %-22s | Tam: %-4s | Cor: %-12s | Qtd: %-3d%s\n",
                            v.sku(), v.size(), v.color(), v.stockQuantity(), alert);
                }
            }
        }
    }

    private void registerProduct() {
        try {
            System.out.println("\n--- Cadastro de Produto ---");
            System.out.print("Nome da Peça (ex: Camisa Polo): ");
            String name = scanner.nextLine().trim();

            System.out.print("Marca (ex: Dudalina): ");
            String brand = scanner.nextLine().trim();

            System.out.println("Categorias disponíveis: SHIRTS, PANTS, SHORTS, DRESSES, SKIRTS, JACKETS, UNDERWEAR, FOOTWEAR, ACCESSORIES");
            System.out.print("Categoria: ");
            String categoryStr = scanner.nextLine().trim().toUpperCase();
            ClothingCategory category = ClothingCategory.valueOf(categoryStr);

            System.out.print("Preço Base (ex: 159.90): ");
            String priceStr = scanner.nextLine().trim();
            Money basePrice = Money.of(priceStr);

            ProductDTO created = catalogService.registerProduct(name, brand, category, basePrice);
            System.out.println("✅ Produto cadastrado com sucesso! ID: " + created.id());

            System.out.print("Deseja adicionar uma variante agora? (S/N): ");
            String addVar = scanner.nextLine().trim().toUpperCase();
            if (addVar.startsWith("S")) {
                addVariantToProduct(created.id());
            }
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Categoria inválida!");
        } catch (DomainValidationException e) {
            System.out.println("❌ Erro de validação: " + e.getMessage());
        }
    }

    private void addVariantToProduct(String productId) {
        try {
            System.out.print("Tamanho (PP, P, M, G, GG, XG, UN, 36..52): ");
            ClothingSize size = ClothingSize.fromString(scanner.nextLine().trim());

            System.out.print("Nome da Cor (ex: Azul Marinho, Preto): ");
            Color color = Color.of(scanner.nextLine().trim());

            System.out.print("Código de Barras EAN (opcional, Enter para pular): ");
            String barcode = scanner.nextLine().trim();

            System.out.print("Quantidade Inicial em Estoque: ");
            int stock = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Estoque Mínimo de Alerta: ");
            int threshold = Integer.parseInt(scanner.nextLine().trim());

            VariantDTO variant = catalogService.addVariant(productId, size, color, barcode, stock, threshold);
            System.out.println("✅ Variante criada com SKU: " + variant.sku());
        } catch (Exception e) {
            System.out.println("❌ Erro ao adicionar variante: " + e.getMessage());
        }
    }

    private void replenishStock() {
        try {
            System.out.print("\nInforme o SKU da peça a repor: ");
            String sku = scanner.nextLine().trim();

            System.out.print("Quantidade a adicionar: ");
            int qty = Integer.parseInt(scanner.nextLine().trim());

            catalogService.replenishStock(sku, qty);
            System.out.printf("✅ Estoque reposto com sucesso! (+%d unidades para %s)\n", qty, sku);
        } catch (Exception e) {
            System.out.println("❌ Erro ao repor estoque: " + e.getMessage());
        }
    }

    private void showLowStockAlerts() {
        System.out.println("\n================ ALERTAS DE ESTOQUE BAIXO ================");
        List<VariantDTO> lowStock = catalogService.listLowStockVariants();
        if (lowStock.isEmpty()) {
            System.out.println("✅ Todos os produtos estão com níveis de estoque saudáveis!");
            return;
        }

        System.out.printf("Foram encontrados %d itens com estoque crítico:\n", lowStock.size());
        for (VariantDTO v : lowStock) {
            System.out.printf("⚠️  SKU: %-22s | Tam: %-3s | Cor: %-12s | Atual: %d | Mínimo: %d\n",
                    v.sku(), v.size(), v.color(), v.stockQuantity(), v.minStockThreshold());
        }
    }

    private void pointOfSale() {
        try {
            System.out.println("\n================ NOVO ATENDIMENTO / PDV ================");
            System.out.print("CPF do Cliente (opcional, Enter para consumidor não identificado): ");
            String cpf = scanner.nextLine().trim();

            OrderDTO order = salesService.createOrder(cpf.isEmpty() ? null : cpf);
            System.out.println("🛒 Venda iniciada! Pedido Nº: " + order.orderId());

            boolean adding = true;
            while (adding) {
                System.out.print("\nInforme o SKU do produto (ou 'F' para fechar carrinho): ");
                String sku = scanner.nextLine().trim();
                if (sku.equalsIgnoreCase("F")) {
                    break;
                }

                System.out.print("Quantidade: ");
                int qty = Integer.parseInt(scanner.nextLine().trim());

                try {
                    order = salesService.addItem(order.orderId(), sku, qty);
                    System.out.printf("✅ Adicionado! Subtotal atual: %s (%d itens)\n",
                            order.formattedSubtotal(), order.totalItems());
                } catch (DomainValidationException e) {
                    System.out.println("❌ " + e.getMessage());
                }
            }

            if (order.totalItems() == 0) {
                System.out.println("Venda cancelada por estar vazia.");
                salesService.cancelOrder(order.orderId());
                return;
            }

            // Discount Selection
            System.out.println("\nEscolha a política de desconto:");
            System.out.println("1. Sem desconto");
            System.out.println("2. Cupom Promocional (10% OFF)");
            System.out.println("3. Promoção de Volume (Leve 3+ peças e ganhe 15% OFF)");
            System.out.print("Opção: ");
            String discChoice = scanner.nextLine().trim();

            switch (discChoice) {
                case "2" -> order = salesService.applyDiscount(order.orderId(), PercentageDiscountStrategy.of(10.0));
                case "3" -> order = salesService.applyDiscount(order.orderId(), VolumeDiscountStrategy.of(3, 15.0));
                default -> order = salesService.applyDiscount(order.orderId(), new NoDiscountStrategy());
            }

            // Payment
            System.out.println("\n---------------- RESUMO DA CONTA ----------------");
            System.out.println("Subtotal: " + order.formattedSubtotal());
            System.out.println("Desconto: " + order.formattedDiscount());
            System.out.println("TOTAL A PAGAR: " + order.formattedTotal());
            System.out.println("-------------------------------------------------");

            System.out.println("Forma de Pagamento:");
            System.out.println("1. PIX");
            System.out.println("2. Cartão de Crédito");
            System.out.println("3. Cartão de Débito");
            System.out.println("4. Dinheiro");
            System.out.print("Escolha: ");
            String payChoice = scanner.nextLine().trim();

            PaymentMethod method = switch (payChoice) {
                case "2" -> PaymentMethod.CREDIT_CARD;
                case "3" -> PaymentMethod.DEBIT_CARD;
                case "4" -> PaymentMethod.CASH;
                default -> PaymentMethod.PIX;
            };

            Money total = Money.of(order.total());
            OrderDTO completedOrder = salesService.checkout(order.orderId(), method, total);

            printReceipt(completedOrder);
        } catch (Exception e) {
            System.out.println("❌ Erro durante a venda: " + e.getMessage());
        }
    }

    private void printReceipt(OrderDTO order) {
        System.out.println("\n========================================================");
        System.out.println("                CUPOM FISCAL / RECIBO                   ");
        System.out.println("========================================================");
        System.out.println("Pedido: " + order.orderId());
        System.out.println("Cliente CPF: " + order.customerCpf());
        System.out.println("Data: " + order.paidAt());
        System.out.println("--------------------------------------------------------");
        System.out.printf("%-20s %-12s %-6s %-10s\n", "ITEM", "SKU", "QTD", "TOTAL");
        System.out.println("--------------------------------------------------------");
        for (OrderItemDTO item : order.items()) {
            System.out.printf("%-20s %-12s %-6d %-10s\n",
                    item.productName(), item.sku(), item.quantity(), item.formattedSubtotal());
        }
        System.out.println("--------------------------------------------------------");
        System.out.printf("Subtotal:  %30s\n", order.formattedSubtotal());
        System.out.printf("Desconto:  %30s\n", order.formattedDiscount());
        System.out.printf("TOTAL:     %30s\n", order.formattedTotal());
        System.out.println("Forma de Pagamento: " + order.paymentMethod());
        System.out.println("Status: " + order.status());
        System.out.println("========================================================");
        System.out.println("             Obrigado pela preferência!                 ");
        System.out.println("========================================================\n");
    }

    private void listOrders() {
        System.out.println("\n================ HISTÓRICO DE PEDIDOS ================");
        List<OrderDTO> orders = salesService.listAllOrders();
        if (orders.isEmpty()) {
            System.out.println("Nenhum pedido registrado.");
            return;
        }

        for (OrderDTO o : orders) {
            System.out.printf("Pedido: %s | Cliente: %s | Status: %-10s | Itens: %d | Total: %s | Pgto: %s\n",
                    o.orderId(), o.customerCpf(), o.status(), o.totalItems(), o.formattedTotal(), o.paymentMethod());
        }
    }

    private void showFinancialSummary() {
        System.out.println("\n================ FECHAMENTO DE CAIXA ================");
        List<OrderDTO> all = salesService.listAllOrders();
        long completedCount = all.stream().filter(o -> o.status().equals("Pago")).count();
        Money revenue = salesService.calculateTotalRevenue();

        System.out.println("Vendas Concluídas: " + completedCount);
        System.out.println("Faturamento Total: " + revenue.formatted());
        System.out.println("=====================================================");
    }

    private void reloadSeedData() {
        DataSeeder.seed(catalogService);
        System.out.println("✅ Dados de demonstração recarregados com sucesso!");
    }
}
