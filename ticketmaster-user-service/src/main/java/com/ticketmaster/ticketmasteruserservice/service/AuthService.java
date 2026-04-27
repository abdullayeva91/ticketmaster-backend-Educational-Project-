package com.ticketmaster.ticketmasteruserservice.service;

import com.ticketmaster.ticketmasteruserservice.dto.request.*;
import com.ticketmaster.ticketmasteruserservice.dto.response.AuthResponse;
import com.ticketmaster.ticketmasteruserservice.dto.response.UserResponse;

public interface AuthService {
    UserResponse register(UserRegistrationRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(TokenRefreshRequest request);
        void forgotPassword(ForgotPasswordRequest request);
        void resetPassword(ResetPasswordRequest request);


}
