package com.exemplo.domain.shared.event;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * In-process event publisher implementing the Observer / Pub-Sub pattern for Domain Events.
 * Decouples aggregate root state mutations from external reactions or cross-context listeners.
 */
public class DomainEventPublisher {

    private static final Map<Class<? extends DomainEvent>, List<Consumer<? extends DomainEvent>>> subscribers = new ConcurrentHashMap<>();

    private DomainEventPublisher() {}

    public static <T extends DomainEvent> void subscribe(Class<T> eventType, Consumer<T> subscriber) {
        subscribers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(subscriber);
    }

    @SuppressWarnings("unchecked")
    public static <T extends DomainEvent> void publish(T event) {
        if (event == null) return;
        List<Consumer<? extends DomainEvent>> handlers = subscribers.get(event.getClass());
        if (handlers != null) {
            for (Consumer<? extends DomainEvent> handler : handlers) {
                ((Consumer<T>) handler).accept(event);
            }
        }
    }

    public static void clearSubscribers() {
        subscribers.clear();
    }
}
