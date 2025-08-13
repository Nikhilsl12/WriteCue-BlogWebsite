package org.coderscrib.blogapp.controller;

import jakarta.validation.Valid;
import org.coderscrib.blogapp.dto.post.PostCreateDto;
import org.coderscrib.blogapp.dto.post.PostResponseDto;
import org.coderscrib.blogapp.dto.post.PostSummaryDto;
import org.coderscrib.blogapp.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }
    // controller methods

    // get all the posts like home feed
    @GetMapping
    public ResponseEntity<Page<PostSummaryDto>> getAllPosts(@PageableDefault(size = 10,sort = "createdAt",direction=Sort.Direction.DESC) Pageable pageable){
        Page<PostSummaryDto> posts = postService.getAllPosts(pageable);
        return ResponseEntity.ok(posts);
    }
    // getting specific post by id
    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDto> getPostById(@PathVariable Long id){
        PostResponseDto post = postService.getPostById(id);
        return ResponseEntity.ok(post);
    }
    // Create a Post
    @PostMapping
    public ResponseEntity<PostResponseDto> createPost( @Valid @RequestBody PostCreateDto dto){
        PostResponseDto post = postService.createPost(dto);
        return ResponseEntity.created((URI.create("/posts/"+post.getId())))
                .body(post);
    }
    // update Post
    @PutMapping("/{id}")
    public ResponseEntity<PostResponseDto> updatePost(@PathVariable Long id, @Valid @RequestBody PostCreateDto dto){
        PostResponseDto post = postService.updatePost(id,dto);
        return ResponseEntity.ok(post);

    }
    //DeletePost
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id){
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
    //Share Post
    @GetMapping("/{id}/share")
    public ResponseEntity<String> sharePost(@PathVariable Long id){
        String url = postService.sharePost(id);
        return ResponseEntity.ok(url);
    }

}
