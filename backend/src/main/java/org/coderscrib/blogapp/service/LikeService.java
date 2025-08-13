package org.coderscrib.blogapp.service;

import org.coderscrib.blogapp.dto.user.UserSummaryDto;
import org.coderscrib.blogapp.entity.Like;
import org.coderscrib.blogapp.entity.Post;
import org.coderscrib.blogapp.entity.User;
import org.coderscrib.blogapp.exception.BadRequestException;
import org.coderscrib.blogapp.exception.ConflictException;
import org.coderscrib.blogapp.exception.ResourceNotFoundException;
import org.coderscrib.blogapp.repository.LikeRepository;
import org.coderscrib.blogapp.repository.PostRepository;
import org.coderscrib.blogapp.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(LikeService.class);

    public LikeService(LikeRepository likeRepository, UserRepository userRepository, 
                      PostRepository postRepository, NotificationService notificationService) {
        this.likeRepository = likeRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.notificationService = notificationService;
    }

    //Like
    public void likePost(Long userId, Long postId) {
        logger.info("Attempting to like post ID: {} by user ID: {}", postId, userId);
        
        logger.debug("Retrieving user with ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("Like operation failed: User not found with ID: {}", userId);
                    return ResourceNotFoundException.create("User", "id", userId);
                });
                
        logger.debug("Retrieving post with ID: {}", postId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    logger.warn("Like operation failed: Post not found with ID: {}", postId);
                    return ResourceNotFoundException.create("Post", "id", postId);
                });

        logger.debug("Checking if user already liked the post");
        if(likeRepository.existsByUserAndPost(user, post)){
            logger.warn("Like operation failed: User {} already liked post {}", user.getUsername(), post.getTitle());
            throw new ConflictException("You have already liked this post");
        }
        
        logger.debug("Creating like entity for user: {} on post: {}", user.getUsername(), post.getTitle());
        Like like = Like.builder()
                .user(user)
                .post(post)
                .build();
                
        likeRepository.save(like);
        logger.info("Post liked successfully: User {} liked post {}", user.getUsername(), post.getTitle());
        
        // Send notification to post author about the like
        logger.debug("Sending notification to post author about the like");
        notificationService.notifyPostLike(post, user);
    }
    //unlike post
    public void unlikePost(Long userId, Long postId) {
        logger.info("Attempting to unlike post ID: {} by user ID: {}", postId, userId);
        
        logger.debug("Retrieving user with ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("Unlike operation failed: User not found with ID: {}", userId);
                    return ResourceNotFoundException.create("User", "id", userId);
                });
                
        logger.debug("Retrieving post with ID: {}", postId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    logger.warn("Unlike operation failed: Post not found with ID: {}", postId);
                    return ResourceNotFoundException.create("Post", "id", postId);
                });
                
        logger.debug("Checking if user has liked the post");
        Optional<Like> like = likeRepository.findByUserAndPost(user, post);
        
        if(like.isEmpty()){
            logger.warn("Unlike operation failed: User {} has not liked post {}", user.getUsername(), post.getTitle());
            throw new BadRequestException("You have not liked this post");
        }
        
        logger.debug("Deleting like for user: {} on post: {}", user.getUsername(), post.getTitle());
        likeRepository.delete(like.get());
        
        logger.info("Post unliked successfully: User {} unliked post {}", user.getUsername(), post.getTitle());
    }
    // get liked users on the post
    public List<UserSummaryDto> findAllLikedUsers(Long postId) {
        logger.info("Retrieving all users who liked post ID: {}", postId);
        
        if (postId == null || postId <= 0) {
            logger.warn("Like users retrieval failed: Invalid post ID: {}", postId);
            throw new BadRequestException("Invalid post ID");
        }
        
        logger.debug("Verifying post exists with ID: {}", postId);
        postRepository.findById(postId)
                .orElseThrow(() -> {
                    logger.warn("Like users retrieval failed: Post not found with ID: {}", postId);
                    return ResourceNotFoundException.create("Post", "id", postId);
                });
                
        logger.debug("Fetching likes for post ID: {}", postId);
        List<UserSummaryDto> likedUsers = Optional.ofNullable(likeRepository.findByPostId(postId))
                .orElse(Collections.emptyList())
                .stream()
                .map(Like::getUser)
                .filter(Objects::nonNull)
                .map(this::toUserSummary)
                .collect(Collectors.toList());
                
        logger.debug("Retrieved {} users who liked post ID: {}", likedUsers.size(), postId);
        return likedUsers;

    }
    // get like Count of the Post
    public int likeCount(Long postId){
        logger.info("Retrieving like count for post ID: {}", postId);
        
        if (postId == null || postId <= 0) {
            logger.warn("Like count retrieval failed: Invalid post ID: {}", postId);
            throw new BadRequestException("Invalid post ID");
        }
        
        logger.debug("Counting likes for post ID: {}", postId);
        int count = likeRepository.countByPostId(postId);
        
        logger.debug("Post ID: {} has {} likes", postId, count);
        return count;
    }
    public UserSummaryDto toUserSummary(User user) {
        logger.debug("Converting User entity to UserSummaryDto");
        
        if (user == null) {
            logger.error("Failed to convert User to Summary DTO: User is null");
            throw new BadRequestException("User cannot be null");
        }
        
        logger.debug("Building UserSummaryDto for user ID: {}", user.getId());
        UserSummaryDto dto = new UserSummaryDto();
        dto.setName(user.getDisplayName());
        dto.setUsername(user.getUsername());
        
        logger.debug("User entity successfully converted to Summary DTO: User ID {}", user.getId());
        return dto;
    }
}
