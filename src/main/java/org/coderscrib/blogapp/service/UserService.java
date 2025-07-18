package org.coderscrib.blogapp.service;

import jakarta.transaction.Transactional;
import org.coderscrib.blogapp.dto.post.PostSummaryDto;
import org.coderscrib.blogapp.dto.user.*;
import org.coderscrib.blogapp.entity.User;
import org.coderscrib.blogapp.exception.ResourceNotFoundException;
import org.coderscrib.blogapp.repository.UserRepository;
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

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, 
                      PostService postService, NotificationService notificationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.postService = postService;
        this.notificationService = notificationService;
    }
    @Transactional
    public UserResponseDto registerUser(UserRegistrationDto dto){
        if(userRepository.existsByUsername(dto.getUsername())){
            throw new IllegalArgumentException("Username is already taken!");
        }
        if(userRepository.existsByEmail(dto.getEmail())){
            throw new IllegalArgumentException("Email is already in use!");
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

        // Send registration notification email
        notificationService.notifyUserRegistration(savedUser);

        return toUserResponseDto(savedUser);
    }

    public UserResponseDto loginUser(UserLoginDto userLoginDto) {
        // First, validate if the input is an email or username
        String usernameOrEmail = userLoginDto.getUsernameOrEmail();
        Optional<User> user;

        if (isEmail(usernameOrEmail)) {
            // Query by email
            user = userRepository.findByEmail(usernameOrEmail);
        } else {
            // Query by username
            user = userRepository.findByUsername(usernameOrEmail);
        }

        // Check if the user exists
        if (user.isEmpty()) {
            throw new ResourceNotFoundException("Invalid username or password");
        }

        // Verify the password
        if (!passwordEncoder.matches(userLoginDto.getPassword(), user.get().getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        // Return the DTO
        return toUserResponseDto(user.get());
    }
    //Update user
    @Transactional
    public UserResponseDto updateUser(Long userId, UserUpdateDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Update username if provided
        if (dto.getUsername() != null && !dto.getUsername().isBlank()) {
            if (!user.getUsername().equals(dto.getUsername()) &&
                    userRepository.existsByUsername(dto.getUsername())) {
                throw new IllegalArgumentException("Username is already taken!");
            }
            user.setUsername(dto.getUsername());
        }

        // Update email if provided
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            if (!user.getEmail().equals(dto.getEmail()) &&
                    userRepository.existsByEmail(dto.getEmail())) {
                throw new IllegalArgumentException("Email is already in use!");
            }
            user.setEmail(dto.getEmail());
        }

        // Update bio if provided
        if (dto.getBio() != null) {
            user.setBio(dto.getBio());
        }
        //update name if given
        if(dto.getDisplayName()!=null && !dto.getDisplayName().isBlank()){
            user.setDisplayName(dto.getDisplayName());
        }

        // Update password if provided
//        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
//            // It need to be modifed later by using otp as of now password can be changed and is bad for user
//            user.setPassword(passwordEncoder.encode(dto.getPassword()));
//        }

        User updatedUser = userRepository.save(user);

        // Send profile update notification email
        notificationService.notifyProfileUpdate(updatedUser);

        return toUserResponseDto(updatedUser);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        userRepository.delete(user);
    }

    public UserResponseDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return toUserResponseDto(user);
    }
    public UserResponseDto getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return toUserResponseDto(user);
    }
//    public Page<UserResponseDto> getAllUsers(Pageable pageable) {
//        return userRepository.findAll(pageable)
//                .map(this::toUserResponseDto);
//    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Incorrect old password");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        User updatedUser = userRepository.save(user);

        // Send password change notification email
        notificationService.notifyUserPasswordChange(updatedUser);
    }

    // Utility method to validate email format
    private boolean isEmail(String input) {
    return input.contains("@") && input.contains("."); // Basic check, can be extended
    }

    private UserResponseDto toUserResponseDto(User savedUser) {
        if (savedUser == null) {
            throw new IllegalArgumentException("User cannot be null");
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
        return dto;
    }



}
