package com.ticketmaster.ticketmasternotificatinservice.event;

public record ForgotPasswordEvent(String email, String token) {
}
