package com.ticketmaster.ticketmasteruserservice.service;

import com.ticketmaster.ticketmasteruserservice.dto.request.UpdateProfileRequest;
import com.ticketmaster.ticketmasteruserservice.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse getUserByEmail(String email);

    UserResponse updateProfile(UpdateProfileRequest request);
    UserResponse getMyProfile();
    boolean existsById(Long id);
    String getEmailById(Long id);
    List<UserResponse> getAllUsers();
}
