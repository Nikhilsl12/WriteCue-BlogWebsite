package org.coderscrib.blogapp.controller;

import jakarta.validation.Valid;
import org.coderscrib.blogapp.dto.post.PostSummaryDto;
import org.coderscrib.blogapp.dto.user.*;
import org.coderscrib.blogapp.service.PostService;
import org.coderscrib.blogapp.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final PostService postService;

    public UserController(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable Long id){
        UserResponseDto user = userService.getUserById(id);
        return new ResponseEntity<>(user,HttpStatus.OK);
    }
    @GetMapping("/{id}/posts")
    public ResponseEntity<Page<PostSummaryDto>> getUserPosts(@PathVariable Long id,
                                                             @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable){
        Page<PostSummaryDto> posts = postService.getUserPosts(id,pageable);
        return ResponseEntity.ok(posts);
    }
    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponseDto> getUserByUsername(@PathVariable String username){
        UserResponseDto user = userService.getUserByUsername(username);
        return new ResponseEntity<>(user,HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@RequestBody @Valid UserRegistrationDto user) {
        UserResponseDto userResponseDto = userService.registerUser(user);
        return new ResponseEntity<>(userResponseDto,HttpStatus.CREATED);
    }
    @PostMapping("/login")
    public ResponseEntity<UserResponseDto> login(@RequestBody @Valid UserLoginDto user) {
        UserResponseDto userResponseDto = userService.loginUser(user);
        return new ResponseEntity<>(userResponseDto,HttpStatus.OK);

    }
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> update(@PathVariable Long id, @RequestBody @Valid UserUpdateDto userUpdateDto) {
        UserResponseDto userResponseDto = userService.updateUser(id,userUpdateDto);
        return new ResponseEntity<>(userResponseDto,HttpStatus.OK);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{id}/change-password")
    public ResponseEntity<String> changePassword(@PathVariable Long id, @RequestBody @Valid ChangePasswordDto changePasswordDto) {
        userService.changePassword(id,changePasswordDto);
        return ResponseEntity.ok("Password changed successfully");
    }

}
