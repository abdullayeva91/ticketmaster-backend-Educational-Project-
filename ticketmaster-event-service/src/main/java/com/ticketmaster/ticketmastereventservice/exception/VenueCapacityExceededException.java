package com.ticketmaster.ticketmastereventservice.exception;

public class VenueCapacityExceededException extends RuntimeException {
    public VenueCapacityExceededException(String message) {
        super(message);
    }
}
