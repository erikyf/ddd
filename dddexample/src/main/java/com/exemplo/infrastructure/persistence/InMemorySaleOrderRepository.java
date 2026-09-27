package com.exemplo.infrastructure.persistence;

import com.exemplo.domain.customer.model.CPF;
import com.exemplo.domain.sales.model.OrderId;
import com.exemplo.domain.sales.model.OrderStatus;
import com.exemplo.domain.sales.model.SaleOrder;
import com.exemplo.domain.sales.repository.SaleOrderRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-Memory thread-safe implementation of SaleOrderRepository.
 */
public class InMemorySaleOrderRepository implements SaleOrderRepository {

    private final Map<OrderId, SaleOrder> storage = new ConcurrentHashMap<>();

    @Override
    public void save(SaleOrder order) {
        if (order == null) {
            throw new IllegalArgumentException("Cannot save null order");
        }
        storage.put(order.getId(), order);
    }

    @Override
    public Optional<SaleOrder> findById(OrderId id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<SaleOrder> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public List<SaleOrder> findByStatus(OrderStatus status) {
        if (status == null) return Collections.emptyList();
        return storage.values().stream()
                .filter(order -> order.getStatus() == status)
                .toList();
    }

    @Override
    public List<SaleOrder> findByCustomerCpf(CPF cpf) {
        if (cpf == null) return Collections.emptyList();
        return storage.values().stream()
                .filter(order -> order.getCustomerCpf().isPresent() && order.getCustomerCpf().get().equals(cpf))
                .toList();
    }

    @Override
    public boolean existsById(OrderId id) {
        return id != null && storage.containsKey(id);
    }

    @Override
    public void delete(OrderId id) {
        if (id != null) {
            storage.remove(id);
        }
    }

    public void clear() {
        storage.clear();
    }
}
