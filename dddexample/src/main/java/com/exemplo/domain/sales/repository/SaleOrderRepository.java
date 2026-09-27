package com.exemplo.domain.sales.repository;

import com.exemplo.domain.customer.model.CPF;
import com.exemplo.domain.sales.model.OrderId;
import com.exemplo.domain.sales.model.OrderStatus;
import com.exemplo.domain.sales.model.SaleOrder;

import java.util.List;
import java.util.Optional;

/**
 * Domain Repository interface for SaleOrder aggregate persistence.
 */
public interface SaleOrderRepository {

    void save(SaleOrder order);

    Optional<SaleOrder> findById(OrderId id);

    List<SaleOrder> findAll();

    List<SaleOrder> findByStatus(OrderStatus status);

    List<SaleOrder> findByCustomerCpf(CPF cpf);

    boolean existsById(OrderId id);

    void delete(OrderId id);
}
