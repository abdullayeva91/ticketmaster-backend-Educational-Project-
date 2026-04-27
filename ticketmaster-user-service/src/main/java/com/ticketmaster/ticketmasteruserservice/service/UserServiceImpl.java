package com.ticketmaster.ticketmasteruserservice.service;

import com.ticketmaster.ticketmasteruserservice.dto.request.UpdateProfileRequest;
import com.ticketmaster.ticketmasteruserservice.dto.response.UserResponse;
import com.ticketmaster.ticketmasteruserservice.exception.ResourceNotFoundException;
import com.ticketmaster.ticketmasteruserservice.mapper.UserMapper;
import com.ticketmaster.ticketmasteruserservice.model.User;
import com.ticketmaster.ticketmasteruserservice.model.UserProfile;
import com.ticketmaster.ticketmasteruserservice.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserResponse getUserByEmail(String email) {
         User user = userRepository.findByEmail(email)
                .orElseThrow(()->new ResourceNotFoundException("bu email ile istifadeci tapilmadi"));
         return  userMapper.toUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateProfile(UpdateProfileRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("İstifadəçi tapılmadı"));
        if (user.getUserProfile() == null) {
            UserProfile newProfile = new UserProfile();
            newProfile.setUser(user);
            user.setUserProfile(newProfile);
        }
        userMapper.updateProfileFromRequest(request, user.getUserProfile());
        User updatedUser = userRepository.save(user);
        return userMapper.toUserResponse(updatedUser);
    }
    @Override
    public UserResponse getMyProfile() {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new RuntimeException("İstifadəçi tapılmadı!"));

        return userMapper.toUserResponse(user);
    }

    @Override
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    @Override
    public String getEmailById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("İstifadəçi tapılmadı: " + id));
        return user.getEmail();
    }
    @Override
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }


}
