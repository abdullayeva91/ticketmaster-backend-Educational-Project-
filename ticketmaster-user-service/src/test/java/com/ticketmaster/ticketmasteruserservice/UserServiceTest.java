package com.ticketmaster.ticketmasteruserservice;

import com.ticketmaster.ticketmasteruserservice.dto.request.UpdateProfileRequest;
import com.ticketmaster.ticketmasteruserservice.dto.response.UserResponse;
import com.ticketmaster.ticketmasteruserservice.exception.ResourceNotFoundException;
import com.ticketmaster.ticketmasteruserservice.mapper.UserMapper;
import com.ticketmaster.ticketmasteruserservice.model.User;
import com.ticketmaster.ticketmasteruserservice.model.UserProfile;
import com.ticketmaster.ticketmasteruserservice.repository.UserRepository;
import com.ticketmaster.ticketmasteruserservice.service.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    // SecurityContext mock üçün köməkçi metod
    private void mockSecurityContext(String email) {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        doReturn(email).when(authentication).getName();
        doReturn(authentication).when(securityContext).getAuthentication();
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void getMyProfile_success() {
        mockSecurityContext("test@test.com");

        User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");

        UserResponse expectedResponse = new UserResponse();
        expectedResponse.setId(1L);

        doReturn(Optional.of(user)).when(userRepository).findByEmail("test@test.com");
        doReturn(expectedResponse).when(userMapper).toUserResponse(user);

        UserResponse result = userService.getMyProfile();

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    public void getMyProfile_whenUserNotFound_throwException() {
        mockSecurityContext("notfound@test.com");

        doReturn(Optional.empty()).when(userRepository).findByEmail("notfound@test.com");

        assertThrows(Exception.class, () -> userService.getMyProfile());
    }

    @Test
    public void updateProfile_success() {
        mockSecurityContext("test@test.com");

        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFirstName("Yeni");
        request.setLastName("Ad");

        User user = new User();
        user.setEmail("test@test.com");
        user.setUserProfile(new UserProfile());

        UserResponse expectedResponse = new UserResponse();
        expectedResponse.setId(1L);

        doReturn(Optional.of(user)).when(userRepository).findByEmail("test@test.com");
        doReturn(user).when(userRepository).save(any());
        doReturn(expectedResponse).when(userMapper).toUserResponse(any());

        UserResponse result = userService.updateProfile(request);

        assertNotNull(result);
        verify(userRepository).save(user);
    }

    @Test
    public void existsById_whenUserExists_returnTrue() {
        doReturn(true).when(userRepository).existsById(1L);

        boolean result = userService.existsById(1L);

        assertTrue(result);
    }

    @Test
    public void existsById_whenUserNotExists_returnFalse() {
        doReturn(false).when(userRepository).existsById(99L);

        boolean result = userService.existsById(99L);

        assertFalse(result);
    }

    @Test
    public void getEmailById_whenUserNotFound_throwException() {
        doReturn(Optional.empty()).when(userRepository).findById(anyLong());

        assertThrows(ResourceNotFoundException.class, () -> userService.getEmailById(1L));
    }
}