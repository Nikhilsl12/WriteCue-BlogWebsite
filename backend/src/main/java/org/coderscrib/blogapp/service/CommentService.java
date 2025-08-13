package org.coderscrib.blogapp.service;

import org.coderscrib.blogapp.dto.comment.CommentCreateDto;
import org.coderscrib.blogapp.dto.comment.CommentResponseDto;
import org.coderscrib.blogapp.dto.comment.CommentSummaryDto;
import org.coderscrib.blogapp.entity.Comment;
import org.coderscrib.blogapp.entity.Post;
import org.coderscrib.blogapp.entity.User;
import org.coderscrib.blogapp.exception.BadRequestException;
import org.coderscrib.blogapp.exception.ResourceNotFoundException;
import org.coderscrib.blogapp.repository.CommentRepository;
import org.coderscrib.blogapp.repository.PostRepository;
import org.coderscrib.blogapp.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);

    public CommentService(CommentRepository commentRepository, PostRepository postRepository, 
                         UserRepository userRepository, NotificationService notificationService) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    // post a comment.
    public CommentResponseDto createComment(CommentCreateDto dto, Long userId, Long postId) {
        logger.info("Attempting to create comment for post ID: {} by user ID: {}", postId, userId);
        
        if (postId == null || postId <= 0) {
            logger.warn("Comment creation failed: Invalid post ID: {}", postId);
            throw new BadRequestException("Invalid post ID");
        }
        if (userId == null || userId <= 0) {
            logger.warn("Comment creation failed: Invalid user ID: {}", userId);
            throw new BadRequestException("Invalid user ID");
        }
        if (dto.getContent() == null || dto.getContent().isBlank()) {
            logger.warn("Comment creation failed: Empty content provided by user ID: {}", userId);
            throw new BadRequestException("Comment content cannot be empty");
        }

        logger.debug("Retrieving post with ID: {}", postId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    logger.warn("Comment creation failed: Post not found with ID: {}", postId);
                    return ResourceNotFoundException.create("Post", "id", postId);
                });
                
        logger.debug("Retrieving user with ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("Comment creation failed: User not found with ID: {}", userId);
                    return ResourceNotFoundException.create("User", "id", userId);
                });

        logger.debug("Building comment entity");
        Comment comment = Comment.builder()
                .content(dto.getContent())
                .user(user)
                .post(post)
                .build();
                
        Comment savedComment = commentRepository.save(comment);
        logger.info("Comment created successfully: ID {}, by user: {}, on post: {}", 
                savedComment.getId(), user.getUsername(), post.getTitle());

        // Send notification to post author about the comment
        logger.debug("Sending notification to post author about the comment");
        notificationService.notifyPostComment(post, user, dto.getContent());
        
        return toCommentResponseDto(savedComment);
    }
    // update a comment
    public CommentResponseDto updateComment(Long commentId, CommentCreateDto dto) {
        logger.info("Attempting to update comment with ID: {}", commentId);
        
        if (commentId == null || commentId <= 0) {
            logger.warn("Comment update failed: Invalid comment ID: {}", commentId);
            throw new BadRequestException("Invalid comment ID");
        }
        
        if (dto.getContent() == null || dto.getContent().isBlank()) {
            logger.warn("Comment update failed: Empty content provided for comment ID: {}", commentId);
            throw new BadRequestException("Comment content cannot be empty");
        }
        
        logger.debug("Retrieving comment with ID: {}", commentId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    logger.warn("Comment update failed: Comment not found with ID: {}", commentId);
                    return ResourceNotFoundException.create("Comment", "id", commentId);
                });
                
        logger.debug("Updating comment content for comment ID: {}", commentId);
        comment.setContent(dto.getContent());
        Comment updatedComment = commentRepository.save(comment);
        
        logger.info("Comment updated successfully: ID {}, by user: {}", 
                updatedComment.getId(), comment.getUser().getUsername());
        return toCommentResponseDto(updatedComment);
    }
    // see a comment
    public CommentResponseDto getCommentById(Long commentId){
        logger.info("Retrieving comment by ID: {}", commentId);
        
        if (commentId == null || commentId <= 0) {
            logger.warn("Comment retrieval failed: Invalid comment ID: {}", commentId);
            throw new BadRequestException("Invalid comment ID");
        }
        
        logger.debug("Searching for comment with ID: {}", commentId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    logger.warn("Comment retrieval failed: Comment not found with ID: {}", commentId);
                    return ResourceNotFoundException.create("Comment", "id", commentId);
                });
                
        logger.debug("Comment found: ID {}, by user: {}", comment.getId(), comment.getUser().getUsername());
        return toCommentResponseDto(comment);
    }
    // see all comments on the post
    public List<CommentSummaryDto> getAllComments(Long postId){
        logger.info("Retrieving all comments for post ID: {}", postId);
        
        if (postId == null || postId <= 0) {
            logger.warn("Comment retrieval failed: Invalid post ID: {}", postId);
            throw new BadRequestException("Invalid post ID");
        }
        
        logger.debug("Verifying post exists with ID: {}", postId);
        postRepository.findById(postId)
                .orElseThrow(() -> {
                    logger.warn("Comment retrieval failed: Post not found with ID: {}", postId);
                    return ResourceNotFoundException.create("Post", "id", postId);
                });
                
        logger.debug("Fetching comments for post ID: {}", postId);
        List<CommentSummaryDto> comments = Optional.ofNullable(commentRepository.findByPostId(postId))
                .orElse(Collections.emptyList())
                .stream()
                .map(this::toCommentSummaryDto)
                .toList();
                
        logger.debug("Retrieved {} comments for post ID: {}", comments.size(), postId);
        return comments;
    }
    // delete a comment
    public void deleteComment(Long commentId){
        logger.info("Attempting to delete comment with ID: {}", commentId);
        
        if (commentId == null || commentId <= 0) {
            logger.warn("Comment deletion failed: Invalid comment ID: {}", commentId);
            throw new BadRequestException("Invalid comment ID");
        }
        
        logger.debug("Retrieving comment with ID: {}", commentId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    logger.warn("Comment deletion failed: Comment not found with ID: {}", commentId);
                    return ResourceNotFoundException.create("Comment", "id", commentId);
                });
        
        String username = comment.getUser().getUsername();
        Long postId = comment.getPost().getId();
        
        logger.debug("Deleting comment ID: {} by user: {} on post ID: {}", commentId, username, postId);
        commentRepository.delete(comment);
        
        logger.info("Comment deleted successfully: ID {}, by user: {}", commentId, username);
    }

    // Utility Methods
    private CommentResponseDto toCommentResponseDto(Comment comment) {
        logger.debug("Converting Comment entity to CommentResponseDto");
        
        if(comment == null) {
            logger.error("Failed to convert Comment to DTO: Comment is null");
            throw new BadRequestException("Comment is not found or is null");
        }
        
        logger.debug("Building CommentResponseDto for comment ID: {}", comment.getId());
        CommentResponseDto dto = CommentResponseDto.builder()
                .id(comment.getId())
                .postId(comment.getPost().getId())
                .userId(comment.getUser().getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
                
        logger.debug("Comment entity successfully converted to DTO: Comment ID {}", comment.getId());
        return dto;
    }
    
    private CommentSummaryDto toCommentSummaryDto(Comment comment) {
        logger.debug("Converting Comment entity to CommentSummaryDto");
        
        if(comment == null) {
            logger.error("Failed to convert Comment to Summary DTO: Comment is null");
            throw new BadRequestException("Comment is not found or is null");
        }
        
        logger.debug("Building CommentSummaryDto for comment ID: {}", comment.getId());
        CommentSummaryDto dto = CommentSummaryDto.builder()
                .displayName(comment.getUser().getDisplayName())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
                
        logger.debug("Comment entity successfully converted to Summary DTO: Comment ID {}", comment.getId());
        return dto;
    }

}
