package com.marketplace.order.exception;

public class InvalidStatusTransitionException extends RuntimeException {
    public InvalidStatusTransitionException(String entityName, String fromStatus, String toStatus) {
        super("Cannot transition " + entityName + " from " + fromStatus + " to " + toStatus);
    }
}
