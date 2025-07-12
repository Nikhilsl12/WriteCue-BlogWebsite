package org.coderscrib.blogapp.service;

import org.coderscrib.blogapp.dto.user.UserSummaryDto;
import org.coderscrib.blogapp.entity.Like;
import org.coderscrib.blogapp.entity.Post;
import org.coderscrib.blogapp.entity.User;
import org.coderscrib.blogapp.exception.ResourceNotFoundException;
import org.coderscrib.blogapp.repository.LikeRepository;
import org.coderscrib.blogapp.repository.PostRepository;
import org.coderscrib.blogapp.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final NotificationService notificationService;

    public LikeService(LikeRepository likeRepository, UserRepository userRepository, 
                      PostRepository postRepository, NotificationService notificationService) {
        this.likeRepository = likeRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.notificationService = notificationService;
    }

    //Like
    public void likePost(Long userId, Long postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        if(likeRepository.existsByUserAndPost(user,post)){
            throw new IllegalArgumentException("You have already liked this post");
        }
        Like like = Like.builder()
                .user(user)
                .post(post)
                .build();
        likeRepository.save(like);

        // Send notification to post author about the like
        notificationService.notifyPostLike(post, user);
    }
    //unlike post
    public void unlikePost(Long userId, Long postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        Optional<Like> like = likeRepository.findByUserAndPost(user,post);
        if(like.isEmpty()){
            throw new IllegalArgumentException("You have not liked this post");
        }
        else likeRepository.delete(like.get());
    }
    // get liked users on the post
    public List<UserSummaryDto> findAllLikedUsers(Long postId) {
        if (postId == null || postId <= 0) {
            throw new IllegalArgumentException("Invalid post ID");
        }
        postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        return Optional.ofNullable(likeRepository.findByPostId(postId))
                .orElse(Collections.emptyList())
                .stream()
                .map(Like::getUser)
                .filter(Objects::nonNull)
                .map(this::toUserSummary)
                .collect(Collectors.toList());

    }
    // get like Count of the Post
    public int likeCount(Long postId){
        return likeRepository.countByPostId(postId);
    }
    public UserSummaryDto toUserSummary( User user) {
        UserSummaryDto dto = new UserSummaryDto();
        dto.setName(user.getDisplayName());
        dto.setUsername(user.getUsername());
        return dto;
    }
}
