package com.exemplo.domain.shared;

/**
 * Thrown when a business invariant or domain validation rule is violated.
 */
public class DomainValidationException extends RuntimeException {
    public DomainValidationException(String message) {
        super(message);
    }
}
