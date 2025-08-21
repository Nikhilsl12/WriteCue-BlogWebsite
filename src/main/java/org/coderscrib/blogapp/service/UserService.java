package org.coderscrib.blogapp.service;

import jakarta.transaction.Transactional;
import org.coderscrib.blogapp.dto.post.PostSummaryDto;
import org.coderscrib.blogapp.dto.user.*;
import org.coderscrib.blogapp.entity.User;
import org.coderscrib.blogapp.exception.ResourceNotFoundException;
import org.coderscrib.blogapp.exception.BadRequestException;
import org.coderscrib.blogapp.exception.ConflictException;
import org.coderscrib.blogapp.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PostService postService;
    private final NotificationService notificationService;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, 
                      PostService postService, NotificationService notificationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.postService = postService;
        this.notificationService = notificationService;
    }
    @Transactional
    public UserResponseDto registerUser(UserRegistrationDto dto){
        logger.info("Attempting to register new user with username: {}", dto.getUsername());
        
        if(userRepository.existsByUsername(dto.getUsername())){
            logger.warn("Registration failed: Username '{}' is already taken", dto.getUsername());
            throw new ConflictException("Username is already taken!");
        }
        if(userRepository.existsByEmail(dto.getEmail())){
            logger.warn("Registration failed: Email '{}' is already in use", dto.getEmail());
            throw new ConflictException("Email is already in use!");
        }
        
        User user = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .displayName(dto.getDisplayName())
                .password(passwordEncoder.encode(dto.getPassword()))
                .bio(dto.getBio())
                .posts(new ArrayList<>())
                .comments(new ArrayList<>())
                .likes(new ArrayList<>())
                .notifications(new ArrayList<>())
                .build();

        User savedUser = userRepository.save(user);
        logger.info("User registered successfully with ID: {}", savedUser.getId());

        // Send registration notification email
        notificationService.notifyUserRegistration(savedUser);
        logger.debug("Registration notification email sent to: {}", savedUser.getEmail());

        return toUserResponseDto(savedUser);
    }

    public UserResponseDto loginUser(UserLoginDto userLoginDto) {
        // First, validate if the input is an email or username
        String usernameOrEmail = userLoginDto.getUsernameOrEmail();
        logger.info("Login attempt with username/email: {}", usernameOrEmail);
        Optional<User> user;

        if (isEmail(usernameOrEmail)) {
            logger.debug("Login input identified as email");
            // Query by email
            user = userRepository.findByEmail(usernameOrEmail);
        } else {
            logger.debug("Login input identified as username");
            // Query by username
            user = userRepository.findByUsername(usernameOrEmail);
        }

        // Check if the user exists
        if (user.isEmpty()) {
            logger.warn("Login failed: User not found with username/email: {}", usernameOrEmail);
            throw new ResourceNotFoundException("Invalid username or password");
        }

        // Verify the password
        if (!passwordEncoder.matches(userLoginDto.getPassword(), user.get().getPassword())) {
            logger.warn("Login failed: Invalid password for user: {}", usernameOrEmail);
            throw new BadRequestException("Invalid username or password");
        }

        logger.info("User logged in successfully: {}, ID: {}", usernameOrEmail, user.get().getId());
        // Return the DTO
        return toUserResponseDto(user.get());
    }
    //Update user
    @Transactional
    public UserResponseDto updateUser(Long userId, UserUpdateDto dto) {
        logger.info("Attempting to update user with ID: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("User update failed: User not found with ID: {}", userId);
                    return new ResourceNotFoundException("User not found");
                });

        // Update username if provided
        if (dto.getUsername() != null && !dto.getUsername().isBlank()) {
            logger.debug("Attempting to update username from '{}' to '{}'", user.getUsername(), dto.getUsername());
            if (!user.getUsername().equals(dto.getUsername()) &&
                    userRepository.existsByUsername(dto.getUsername())) {
                logger.warn("Username update failed: Username '{}' is already taken", dto.getUsername());
                throw new ConflictException("Username is already taken!");
            }
            user.setUsername(dto.getUsername());
            logger.debug("Username updated successfully");
        }

        // Update email if provided
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            logger.debug("Attempting to update email from '{}' to '{}'", user.getEmail(), dto.getEmail());
            if (!user.getEmail().equals(dto.getEmail()) &&
                    userRepository.existsByEmail(dto.getEmail())) {
                logger.warn("Email update failed: Email '{}' is already in use", dto.getEmail());
                throw new ConflictException("Email is already in use!");
            }
            user.setEmail(dto.getEmail());
            logger.debug("Email updated successfully");
        }

        // Update bio if provided
        if (dto.getBio() != null) {
            logger.debug("Updating user bio");
            user.setBio(dto.getBio());
        }
        
        //update name if given
        if(dto.getDisplayName()!=null && !dto.getDisplayName().isBlank()){
            logger.debug("Updating display name from '{}' to '{}'", user.getDisplayName(), dto.getDisplayName());
            user.setDisplayName(dto.getDisplayName());
        }

        // Update password if provided
//        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
//            // It need to be modifed later by using otp as of now password can be changed and is bad for user
//            user.setPassword(passwordEncoder.encode(dto.getPassword()));
//        }

        User updatedUser = userRepository.save(user);
        logger.info("User updated successfully: ID {}", updatedUser.getId());

        // Send profile update notification email
        notificationService.notifyProfileUpdate(updatedUser);
        logger.debug("Profile update notification email sent to: {}", updatedUser.getEmail());

        return toUserResponseDto(updatedUser);
    }

    @Transactional
    public void deleteUser(Long userId) {
        logger.info("Attempting to delete user with ID: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("User deletion failed: User not found with ID: {}", userId);
                    return new ResourceNotFoundException("User not found");
                });

        String username = user.getUsername();
        userRepository.delete(user);
        logger.info("User deleted successfully: {} (ID: {})", username, userId);
    }

    public UserResponseDto getUserById(Long userId) {
        logger.info("Retrieving user by ID: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("User retrieval failed: User not found with ID: {}", userId);
                    return new ResourceNotFoundException("User not found");
                });
                
        logger.debug("User found: {} (ID: {})", user.getUsername(), userId);
        return toUserResponseDto(user);
    }
    
    public UserResponseDto getUserByUsername(String username) {
        logger.info("Retrieving user by username: {}", username);
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("User retrieval failed: User not found with username: {}", username);
                    return new ResourceNotFoundException("User not found");
                });
                
        logger.debug("User found: {} (ID: {})", username, user.getId());
        return toUserResponseDto(user);
    }
//    public Page<UserResponseDto> getAllUsers(Pageable pageable) {
//        return userRepository.findAll(pageable)
//                .map(this::toUserResponseDto);
//    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordDto dto) {
        logger.info("Password change requested for user ID: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("Password change failed: User not found with ID: {}", userId);
                    return new ResourceNotFoundException("User not found");
                });

        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            logger.warn("Password change failed: Incorrect old password for user ID: {}", userId);
            throw new BadRequestException("Incorrect old password");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        User updatedUser = userRepository.save(user);
        logger.info("Password changed successfully for user ID: {}", userId);

        // Send password change notification email
        notificationService.notifyUserPasswordChange(updatedUser);
        logger.debug("Password change notification email sent to: {}", updatedUser.getEmail());
    }

    // Utility method to validate email format
    private boolean isEmail(String input) {
    return input.contains("@") && input.contains("."); // Basic check, can be extended
    }

    private UserResponseDto toUserResponseDto(User savedUser) {
        logger.debug("Converting User entity to UserResponseDto");
        
        if (savedUser == null) {
            logger.error("Failed to convert User to DTO: User is null");
            throw new BadRequestException("User cannot be null");
        }
        
        UserResponseDto dto = new UserResponseDto();
        dto.setId(savedUser.getId());
        dto.setUsername(savedUser.getUsername());
        dto.setDisplayName(savedUser.getDisplayName());
        dto.setEmail(savedUser.getEmail());
        dto.setBio(savedUser.getBio());
        dto.setCreatedAt(savedUser.getCreatedAt());
        
        List<PostSummaryDto> postDto = Optional.ofNullable(savedUser.getPosts())
                .orElse(Collections.emptyList())
                .stream()
                .map(postService::toPostSummaryDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        dto.setPosts(postDto);
        logger.debug("User entity successfully converted to DTO: User ID {}", savedUser.getId());
        return dto;
    }



}
