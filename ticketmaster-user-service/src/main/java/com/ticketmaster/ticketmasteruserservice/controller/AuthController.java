package com.ticketmaster.ticketmasteruserservice.controller;

import com.ticketmaster.ticketmasteruserservice.dto.request.ForgotPasswordRequest;
import com.ticketmaster.ticketmasteruserservice.dto.request.LoginRequest;
import com.ticketmaster.ticketmasteruserservice.dto.request.ResetPasswordRequest;
import com.ticketmaster.ticketmasteruserservice.dto.request.UserRegistrationRequest;
import com.ticketmaster.ticketmasteruserservice.dto.response.AuthResponse;
import com.ticketmaster.ticketmasteruserservice.dto.response.UserResponse;
import com.ticketmaster.ticketmasteruserservice.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }
   @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRegistrationRequest request) {
     UserResponse response = authService.register(request);
     return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok("Şifrə sıfırlama kodu göndərildi.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok("Şifrə uğurla yeniləndi.");
    }


}
