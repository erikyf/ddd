package com.exemplo.application.sales;

import com.exemplo.application.dto.OrderDTO;
import com.exemplo.application.dto.SalesMapper;
import com.exemplo.domain.catalog.model.Product;
import com.exemplo.domain.catalog.model.ProductVariant;
import com.exemplo.domain.catalog.model.SKU;
import com.exemplo.domain.catalog.repository.ProductRepository;
import com.exemplo.domain.customer.model.CPF;
import com.exemplo.domain.sales.model.*;
import com.exemplo.domain.sales.policy.DiscountStrategy;
import com.exemplo.domain.sales.repository.SaleOrderRepository;
import com.exemplo.domain.shared.DomainValidationException;
import com.exemplo.domain.shared.Money;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Application service orchestrating Sales and POS Checkout use cases.
 * Coordinates between the Product aggregate and SaleOrder aggregate.
 */
public class SalesService {

    private final SaleOrderRepository orderRepository;
    private final ProductRepository productRepository;

    public SalesService(SaleOrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = Objects.requireNonNull(orderRepository, "SaleOrderRepository cannot be null");
        this.productRepository = Objects.requireNonNull(productRepository, "ProductRepository cannot be null");
    }

    public OrderDTO createOrder(String rawCpf) {
        OrderId id = OrderId.generate();
        CPF customerCpf = (rawCpf != null && !rawCpf.trim().isEmpty()) ? CPF.of(rawCpf) : null;
        SaleOrder order = SaleOrder.create(id, customerCpf);
        orderRepository.save(order);
        return SalesMapper.toDTO(order);
    }

    public OrderDTO addItem(String rawOrderId, String rawSku, int quantity) {
        SaleOrder order = getOrderOrThrow(rawOrderId);
        SKU sku = SKU.of(rawSku);

        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new DomainValidationException("Product not found with SKU: " + rawSku));

        ProductVariant variant = product.getVariant(sku);
        if (variant.getStockQuantity() < quantity) {
            throw new DomainValidationException(String.format(
                    "Insufficient stock for SKU %s. Requested: %d, Available: %d",
                    sku.getValue(), quantity, variant.getStockQuantity()
            ));
        }

        order.addItem(sku, product.getName(), product.getBasePrice(), quantity);
        orderRepository.save(order);

        return SalesMapper.toDTO(order);
    }

    public OrderDTO removeItem(String rawOrderId, String rawSku) {
        SaleOrder order = getOrderOrThrow(rawOrderId);
        order.removeItem(SKU.of(rawSku));
        orderRepository.save(order);
        return SalesMapper.toDTO(order);
    }

    public OrderDTO applyDiscount(String rawOrderId, DiscountStrategy strategy) {
        SaleOrder order = getOrderOrThrow(rawOrderId);
        order.applyDiscountStrategy(strategy);
        orderRepository.save(order);
        return SalesMapper.toDTO(order);
    }

    /**
     * Executes the checkout process: confirms order, deducts inventory across aggregates, and processes payment.
     */
    public OrderDTO checkout(String rawOrderId, PaymentMethod method, Money amountPaid) {
        SaleOrder order = getOrderOrThrow(rawOrderId);

        // 1. Confirm the order
        order.confirm();

        // 2. Decrement stock from each product variant in the catalog bounded context
        for (OrderItem item : order.getItems()) {
            Product product = productRepository.findBySku(item.getSku())
                    .orElseThrow(() -> new DomainValidationException("Product not found for SKU: " + item.getSku()));
            product.deductStock(item.getSku(), item.getQuantity());
            productRepository.save(product);

            ProductVariant variant = product.getVariant(item.getSku());
            if (variant.isLowStock()) {
                com.exemplo.domain.shared.event.DomainEventPublisher.publish(
                        new com.exemplo.domain.catalog.event.StockLowEvent(variant.getSku(), variant.getStockQuantity(), variant.getMinStockThreshold())
                );
            }
        }

        // 3. Process and record payment
        order.pay(method, amountPaid);
        orderRepository.save(order);

        // 4. Publish OrderPaidEvent
        com.exemplo.domain.shared.event.DomainEventPublisher.publish(
                new com.exemplo.domain.sales.event.OrderPaidEvent(order.getId(), order.calculateTotal(), method, order.getTotalItemCount())
        );

        return SalesMapper.toDTO(order);
    }

    public OrderDTO cancelOrder(String rawOrderId) {
        SaleOrder order = getOrderOrThrow(rawOrderId);
        order.cancel();
        orderRepository.save(order);
        return SalesMapper.toDTO(order);
    }

    public List<OrderDTO> listAllOrders() {
        return orderRepository.findAll().stream()
                .map(SalesMapper::toDTO)
                .toList();
    }

    public Optional<OrderDTO> findById(String rawOrderId) {
        return orderRepository.findById(OrderId.of(rawOrderId)).map(SalesMapper::toDTO);
    }

    public Money calculateTotalRevenue() {
        Money total = Money.zero();
        for (SaleOrder order : orderRepository.findByStatus(OrderStatus.PAID)) {
            total = total.add(order.calculateTotal());
        }
        return total;
    }

    private SaleOrder getOrderOrThrow(String rawOrderId) {
        return orderRepository.findById(OrderId.of(rawOrderId))
                .orElseThrow(() -> new DomainValidationException("Order not found with ID: " + rawOrderId));
    }
}
