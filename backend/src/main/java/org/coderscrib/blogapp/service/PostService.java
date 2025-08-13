package org.coderscrib.blogapp.service;

import org.coderscrib.blogapp.dto.post.PostCreateDto;
import org.coderscrib.blogapp.dto.post.PostResponseDto;
import org.coderscrib.blogapp.dto.post.PostSummaryDto;
import org.coderscrib.blogapp.entity.Comment;
import org.coderscrib.blogapp.entity.Like;
import org.coderscrib.blogapp.entity.Post;
import org.coderscrib.blogapp.entity.User;
import org.coderscrib.blogapp.exception.ResourceNotFoundException;
import org.coderscrib.blogapp.repository.PostRepository;
import org.coderscrib.blogapp.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class PostService {
    @Value("${app.base-url}")
    private String baseUrl;

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(PostService.class);

    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository= userRepository;
    }
    public PostSummaryDto toPostSummaryDto(Post post) {
        return new PostSummaryDto(post.getId(), post.getTitle(), post.getCreatedAt());
    }

    //Create Post
    public PostResponseDto createPost(PostCreateDto dto){
        User user = userRepository.findById(dto.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Post post = Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .author(user)
                .likes(new ArrayList<>())
                .comments(new ArrayList<>())
                .build();

        postRepository.save(post);
        logger.info("Post created with id {}", post.getId());
        return toPostResponseDto(post);
    }
    // Update Post
    public PostResponseDto updatePost(Long postId,PostCreateDto dto){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
            post.setTitle(dto.getTitle());
        }
        if (dto.getContent() != null && !dto.getContent().isBlank()) {
            post.setContent(dto.getContent());
        }

        postRepository.save(post);
        logger.info("Post updated with id {}", post.getId());
        return toPostResponseDto(post);
    }
    //Delete Post
    public void deletePost(Long postId){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        logger.info("Post deleted with id {}", post.getId());
        postRepository.delete(post);
    }

    // View Post
    public PostResponseDto getPostById(Long postId){
        Post post = postRepository.findById(postId)
                .orElseThrow(()->new ResourceNotFoundException("No Post Found"));
        logger.info("Post found with id {}", post.getId());
        return toPostResponseDto(post);
    }
    public Page<PostSummaryDto> getAllPosts(Pageable pageable) {
        logger.info("Getting all posts with page {}", pageable.getPageNumber());
        return postRepository.findAll(pageable)
                .map(this::toPostSummaryDto);
    }

    // Share Post
    public String sharePost(Long postId){
        Post post = postRepository.findById(postId)
                .orElseThrow(()->new ResourceNotFoundException("No Post Found"));
        String url = baseUrl+"/posts/"+postId;
        logger.info("Post share with id {}", post.getId());
        return url;
    }
    // view post of users by id
    public Page<PostSummaryDto> getUserPosts(Long userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }
        logger.info("Getting user posts of user {} with page {}", userId, pageable.getPageNumber());
        return postRepository.findByAuthor_Id(userId,pageable).map(this::toPostSummaryDto);
    }

    // utility methods
    private PostResponseDto toPostResponseDto(Post post) {
        if(post == null) throw new ResourceNotFoundException("Post is empty");

        PostResponseDto dto = new PostResponseDto();
        List<Comment> comments = post.getComments();
        List<Like> likes = post.getLikes();

        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setAuthorId(post.getAuthor().getId());
        dto.setDisplayName(post.getAuthor().getDisplayName());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());
        dto.setCommentCount(comments.size());
        dto.setLikeCount(likes.size());
        return dto;
    }
}
