package com.exemplo.domain.shared.event;

import java.time.Instant;

/**
 * Marker interface for all Domain Events in the application.
 * Domain events represent something meaningful that has occurred in the domain.
 */
public interface DomainEvent {
    Instant occurredOn();
}
