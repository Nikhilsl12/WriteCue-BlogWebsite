package org.coderscrib.blogapp.service;

import org.coderscrib.blogapp.dto.user.UserRegistrationDto;
import org.coderscrib.blogapp.dto.user.UserResponseDto;
import org.coderscrib.blogapp.entity.User;
import org.coderscrib.blogapp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PostService postService;

    @InjectMocks
    private UserService userService;

    @Test
    public void testRegisterUser_ShouldNotThrowNullPointerException() {
        // Arrange
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setUsername("testuser");
        registrationDto.setEmail("test@example.com");
        registrationDto.setPassword("password");
        registrationDto.setBio("Test bio");

        User savedUser = User.builder()
                .id(1L)
                .username(registrationDto.getUsername())
                .email(registrationDto.getEmail())
                .password("encodedPassword")
                .bio(registrationDto.getBio())
                .build();

        when(userRepository.existsByUsername(registrationDto.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registrationDto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registrationDto.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act & Assert
        assertDoesNotThrow(() -> {
            UserResponseDto responseDto = userService.registerUser(registrationDto);
            assertNotNull(responseDto);
            System.out.println("[DEBUG_LOG] User registration successful: " + responseDto.getUsername());
        });
    }
}