package com.ticketmaster.ticketmasteruserservice.service;

import com.ticketmaster.ticketmasteruserservice.dto.request.*;
import com.ticketmaster.ticketmasteruserservice.dto.response.AuthResponse;
import com.ticketmaster.ticketmasteruserservice.dto.response.UserResponse;
import com.ticketmaster.ticketmasteruserservice.enums.Role;
import com.ticketmaster.ticketmasteruserservice.exception.InvalidCredentialsException;
import com.ticketmaster.ticketmasteruserservice.exception.ResourceNotFoundException;
import com.ticketmaster.ticketmasteruserservice.exception.UserAlreadyExistsException;
import com.ticketmaster.ticketmasteruserservice.kafka.UserEventProducer;
import com.ticketmaster.ticketmasteruserservice.mapper.UserMapper;
import com.ticketmaster.ticketmasteruserservice.model.User;
import com.ticketmaster.ticketmasteruserservice.model.UserProfile;
import com.ticketmaster.ticketmasteruserservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final UserEventProducer userEventProducer;

    @Override
    @Transactional
    public UserResponse register(UserRegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Bu email artıq mövcuddur: " + request.getEmail());
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);

        UserProfile userProfile = new UserProfile();
        userProfile.setFirstName(request.getFirstName());
        userProfile.setLastName(request.getLastName());
        userProfile.setPhoneNumber(request.getPhoneNumber());
        userProfile.setBirthDate(request.getBirthDate());

        userProfile.setUser(user);
        user.setUserProfile(userProfile);

        User savedUser = userRepository.save(user);
        String fullName = request.getFirstName() + " " + request.getLastName();

        userEventProducer.sendUserRegisteredEvent(savedUser.getId(), savedUser.getEmail(), fullName);

        return userMapper.toUserResponse(savedUser);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("İstifadəçi tapılmadı!"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Şifrə səhvdir!");
        }

        String accessToken = jwtService.generateToken(user.getEmail(), user.getRole().name());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    @Override
    public AuthResponse refreshToken(TokenRefreshRequest request) {
        String refreshToken = request.getRefreshToken();
        String userEmail = jwtService.extractUsername(refreshToken);

        if (userEmail != null) {
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("İstifadəçi tapılmadı"));

            if (jwtService.isTokenValid(refreshToken, user)) {
                String newAccessToken = jwtService.generateToken(user.getEmail(), user.getRole().name());

                return AuthResponse.builder()
                        .accessToken(newAccessToken)
                        .refreshToken(refreshToken)
                        .build();
            }
        }
        throw new InvalidCredentialsException("Refresh token etibarlı deyil!");
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("İstifadəçi tapılmadı"));

        String token = String.format("%06d", new Random().nextInt(999999));

        user.setResetToken(token);
        user.setTokenExpiration(LocalDateTime.now().plusMinutes(15));

        userRepository.save(user);

        userEventProducer.sendForgotPasswordEvent(user.getEmail(), token);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByResetToken(request.getToken())
                .orElseThrow(() -> new ResourceNotFoundException("Daxil etdiyiniz kod səhvdir və ya tapılmadı!"));

        if (user.getTokenExpiration().isBefore(LocalDateTime.now())) {
            throw new InvalidCredentialsException("Kodun etibarlılıq vaxtı bitib!");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetToken(null);
        user.setTokenExpiration(null);

        userRepository.save(user);

        userEventProducer.sendPasswordChangedEvent(user.getEmail());
    }
}