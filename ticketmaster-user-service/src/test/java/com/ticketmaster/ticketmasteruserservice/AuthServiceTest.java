package com.ticketmaster.ticketmasteruserservice;

import com.ticketmaster.ticketmasteruserservice.dto.request.LoginRequest;
import com.ticketmaster.ticketmasteruserservice.dto.request.UserRegistrationRequest;
import com.ticketmaster.ticketmasteruserservice.dto.response.AuthResponse;
import com.ticketmaster.ticketmasteruserservice.dto.response.UserResponse;
import com.ticketmaster.ticketmasteruserservice.enums.Role;
import com.ticketmaster.ticketmasteruserservice.exception.InvalidCredentialsException;
import com.ticketmaster.ticketmasteruserservice.exception.UserAlreadyExistsException;
import com.ticketmaster.ticketmasteruserservice.kafka.UserEventProducer;
import com.ticketmaster.ticketmasteruserservice.mapper.UserMapper;
import com.ticketmaster.ticketmasteruserservice.model.User;
import com.ticketmaster.ticketmasteruserservice.repository.UserRepository;
import com.ticketmaster.ticketmasteruserservice.service.AuthServiceImpl;
import com.ticketmaster.ticketmasteruserservice.service.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserMapper userMapper;
    @Mock
    private JwtService jwtService;
    @Mock
    private UserEventProducer userEventProducer;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    public void register_whenEmailAlreadyExists_throwException() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setEmail("test@test.com");

        doReturn(true).when(userRepository).existsByEmail(anyString());

        assertThrows(UserAlreadyExistsException.class, () -> authService.register(request));
    }

    @Test
    public void register_success() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setEmail("test@test.com");
        request.setPassword("123456");
        request.setUsername("testuser");
        request.setFirstName("Test");
        request.setLastName("User");
        request.setPhoneNumber("+994501234567");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail("test@test.com");
        savedUser.setRole(Role.USER);

        UserResponse expectedResponse = new UserResponse();
        expectedResponse.setId(1L);

        doReturn(false).when(userRepository).existsByEmail(anyString());
        doReturn("encodedPassword").when(passwordEncoder).encode(anyString());
        doReturn(savedUser).when(userRepository).save(any());
        doReturn(expectedResponse).when(userMapper).toUserResponse(any());

        UserResponse result = authService.register(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    public void login_whenUserNotFound_throwException() {
        LoginRequest request = new LoginRequest();
        request.setEmail("notfound@test.com");
        request.setPassword("123456");

        doReturn(Optional.empty()).when(userRepository).findByEmail(anyString());

        assertThrows(Exception.class, () -> authService.login(request));
    }

    @Test
    public void login_whenPasswordWrong_throwException() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@test.com");
        request.setPassword("wrongpassword");

        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("encodedPassword");
        user.setRole(Role.USER);

        doReturn(Optional.of(user)).when(userRepository).findByEmail(anyString());
        doReturn(false).when(passwordEncoder).matches(anyString(), anyString());

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    }

    @Test
    public void login_success() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@test.com");
        request.setPassword("123456");

        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("encodedPassword");
        user.setRole(Role.USER);

        doReturn(Optional.of(user)).when(userRepository).findByEmail(anyString());
        doReturn(true).when(passwordEncoder).matches(anyString(), anyString());
        doReturn("accessToken").when(jwtService).generateToken(anyString(), anyString());
        doReturn("refreshToken").when(jwtService).generateRefreshToken(anyString());

        AuthResponse result = authService.login(request);

        assertNotNull(result);
        assertEquals("accessToken", result.getAccessToken());
        assertEquals("test@test.com", result.getEmail());
    }
}