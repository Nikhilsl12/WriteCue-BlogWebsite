package org.coderscrib.blogapp.controller;

import jakarta.validation.Valid;
import org.coderscrib.blogapp.dto.comment.CommentCreateDto;
import org.coderscrib.blogapp.dto.comment.CommentResponseDto;
import org.coderscrib.blogapp.dto.comment.CommentSummaryDto;
import org.coderscrib.blogapp.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentService commentService;
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/user/{userId}/post/{postId}")
    public ResponseEntity<CommentResponseDto> createComment(@RequestBody @Valid CommentCreateDto dto, @PathVariable Long userId, @PathVariable Long postId){
        CommentResponseDto response = commentService.createComment(dto,userId,postId);
        return ResponseEntity.created(URI.create("/comments/"+response.getId()))
                .body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentResponseDto> updateComment(@PathVariable Long id, @RequestBody @Valid CommentCreateDto dto){
        CommentResponseDto response = commentService.updateComment(id,dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id){
        commentService.deleteComment(id);
        return ResponseEntity.ok().build();
    }

    // see a single comment
    @GetMapping("/{id}")
    public ResponseEntity<CommentResponseDto> getCommentById(@PathVariable Long id){
        CommentResponseDto response = commentService.getCommentById(id);
        return ResponseEntity.ok(response);
    }
    // see all comments on post
    @GetMapping("/post/{id}")
    public ResponseEntity<List<CommentSummaryDto>> getAllComments(@PathVariable Long id){
        List<CommentSummaryDto> response = commentService.getAllComments(id);
        return ResponseEntity.ok(response);
    }
}
