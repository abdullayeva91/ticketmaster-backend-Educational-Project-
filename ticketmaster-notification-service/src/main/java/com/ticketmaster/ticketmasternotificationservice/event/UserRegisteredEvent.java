package com.ticketmaster.ticketmasternotificatinservice.event;

public record UserRegisteredEvent(Long userId, String email, String fullName) {
}