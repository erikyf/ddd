package com.exemplo.domain.catalog.event;

import com.exemplo.domain.catalog.model.SKU;
import com.exemplo.domain.shared.event.DomainEvent;

import java.time.Instant;
import java.util.Objects;

/**
 * Domain Event emitted when an inventory variant reaches or drops below its minimum safety threshold.
 */
public record StockLowEvent(
        SKU sku,
        int currentStock,
        int minThreshold,
        Instant occurredOn
) implements DomainEvent {

    public StockLowEvent(SKU sku, int currentStock, int minThreshold) {
        this(
                Objects.requireNonNull(sku, "SKU cannot be null"),
                currentStock,
                minThreshold,
                Instant.now()
        );
    }
}
