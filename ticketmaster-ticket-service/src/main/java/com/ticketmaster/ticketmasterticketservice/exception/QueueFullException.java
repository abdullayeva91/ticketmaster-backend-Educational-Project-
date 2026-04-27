package com.ticketmaster.ticketmasterticketservice.exception;

public class QueueFullException extends RuntimeException {
    public QueueFullException(String message) {
        super(message);
    }
}
