package com.marketplace.auth.exception;

public class InvalidStatusTransitionException extends RuntimeException {
    public InvalidStatusTransitionException(String entityName, String fromStatus, String toStatus) {
        super("Cannot transition " + entityName + " from " + fromStatus + " to " + toStatus);
    }
}
