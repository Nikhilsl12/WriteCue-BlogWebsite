package org.coderscrib.blogapp.service;

import org.coderscrib.blogapp.dto.comment.CommentCreateDto;
import org.coderscrib.blogapp.dto.comment.CommentResponseDto;
import org.coderscrib.blogapp.dto.comment.CommentSummaryDto;
import org.coderscrib.blogapp.entity.Comment;
import org.coderscrib.blogapp.entity.Post;
import org.coderscrib.blogapp.entity.User;
import org.coderscrib.blogapp.exception.BadRequestException;
import org.coderscrib.blogapp.repository.CommentRepository;
import org.coderscrib.blogapp.repository.PostRepository;
import org.coderscrib.blogapp.repository.UserRepository;
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

    public CommentService(CommentRepository commentRepository, PostRepository postRepository, 
                         UserRepository userRepository, NotificationService notificationService) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    // post a comment.
    public CommentResponseDto createComment(CommentCreateDto dto, Long userId, Long postId) {
        if (postId == null || postId <= 0) {
            throw new IllegalArgumentException("Invalid post ID");
        }
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        if (dto.getContent() == null || dto.getContent().isBlank()) {
            throw new IllegalArgumentException("Comment content cannot be empty");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException("User not found"));

        Comment comment = Comment.builder()
                .content(dto.getContent())
                .user(user)
                .post(post)
                .build();
        Comment savedComment = commentRepository.save(comment);

        // Send notification to post author about the comment
        notificationService.notifyPostComment(post, user, dto.getContent());

        return toCommentResponseDto(savedComment);
    }
    // update a comment
    public CommentResponseDto updateComment(Long commentId, CommentCreateDto dto) {
        if (dto.getContent() == null || dto.getContent().isBlank()) {
            throw new BadRequestException("Comment content cannot be empty");
        }
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        comment.setContent(dto.getContent());
        commentRepository.save(comment);
        return toCommentResponseDto(comment);
    }
    // see a comment
    public CommentResponseDto getCommentById(Long commentId){
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()->new IllegalArgumentException("No Comment Found"));
        return toCommentResponseDto(comment);
    }
    // see all comments on the post
    public List<CommentSummaryDto> getAllComments(Long postId){
        if (postId == null || postId <= 0) {
            throw new BadRequestException("Invalid post ID");
        }
        postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        return Optional.ofNullable(commentRepository.findByPostId(postId))
                .orElse(Collections.emptyList())
                .stream()
                .map(this::toCommentSummaryDto)
                .toList();
    }
    // delete a comment
    public void deleteComment(Long commentId){
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()-> new IllegalArgumentException("No comment found"));
        commentRepository.delete(comment);
    }

    // Utility Methods
    private CommentResponseDto toCommentResponseDto(Comment comment) {
        if(comment==null) throw new IllegalArgumentException("Comment is not found or is null");

        return CommentResponseDto.builder()
                .id(comment.getId())
                .postId(comment.getPost().getId())
                .userId(comment.getUser().getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
    private CommentSummaryDto toCommentSummaryDto(Comment comment) {
        if(comment==null) throw new IllegalArgumentException("Comment is not found or is null");
        return CommentSummaryDto.builder()
                .displayName(comment.getUser().getDisplayName())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }

}
