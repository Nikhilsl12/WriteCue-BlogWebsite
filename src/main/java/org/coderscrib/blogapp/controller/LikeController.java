package org.coderscrib.blogapp.controller;

import org.coderscrib.blogapp.dto.user.UserSummaryDto;
import org.coderscrib.blogapp.service.LikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/likes")
public class LikeController {
    private final LikeService likeService;
    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping("/user/{userId}/post/{postId}")
    public ResponseEntity<Void> createLike(@PathVariable Long userId, @PathVariable Long postId){
        likeService.likePost(userId,postId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/user/{userId}/post/{postId}")
    public ResponseEntity<Void> deleteLike(@PathVariable Long userId, @PathVariable Long postId){
        likeService.unlikePost(userId,postId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<UserSummaryDto>> likedUsers(@PathVariable Long postId){
        return ResponseEntity.ok(likeService.findAllLikedUsers(postId));
    }

    @GetMapping("/post/{postId}/count")
    public ResponseEntity<Integer> countLikes(@PathVariable Long postId){
        return ResponseEntity.ok(likeService.likeCount(postId));
    }

}
