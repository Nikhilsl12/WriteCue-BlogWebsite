package org.coderscrib.blogapp.exception;

/**
 * This class provides examples of how to use the custom exceptions in the application.
 * It is not meant to be used in production, but rather as a reference for developers.
 */
public class ExceptionUsageExample {

    /**
     * Example of using ResourceNotFoundException.
     * 
     * @param id the ID of the resource to find
     * @return a dummy string (this method always throws an exception)
     * @throws ResourceNotFoundException if the resource is not found
     */
    public String findResourceById(Long id) {
        // Example 1: Using the create factory method
        if (id == null) {
            throw ResourceNotFoundException.create("Resource", "id", "null");
        }
        
        // Example 2: Using the constructor directly
        if (id <= 0) {
            throw new ResourceNotFoundException("Resource not found with id: " + id);
        }
        
        // This is just an example, so we always throw an exception
        throw ResourceNotFoundException.create("Resource", "id", id);
    }
    
    /**
     * Example of using BadRequestException.
     * 
     * @param username the username to validate
     * @throws BadRequestException if the username is invalid
     */
    public void validateUsername(String username) {
        // Example: Validating input parameters
        if (username == null || username.isEmpty()) {
            throw new BadRequestException("Username cannot be empty");
        }
        
        if (username.length() < 3) {
            throw new BadRequestException("Username must be at least 3 characters long");
        }
        
        // Example: Checking if username is already taken
        if ("admin".equals(username)) {
            throw new BadRequestException("Username is already taken");
        }
    }
    
    /**
     * Example of using ConflictException.
     * 
     * @param userId the ID of the user
     * @param postId the ID of the post
     * @throws ConflictException if the user has already liked the post
     */
    public void likePost(Long userId, Long postId) {
        // Example: Checking for conflicts
        boolean alreadyLiked = checkIfAlreadyLiked(userId, postId);
        if (alreadyLiked) {
            throw new ConflictException("You have already liked this post");
        }
    }
    
    /**
     * Helper method to check if a user has already liked a post.
     * This is just a dummy implementation for the example.
     * 
     * @param userId the ID of the user
     * @param postId the ID of the post
     * @return true if the user has already liked the post, false otherwise
     */
    private boolean checkIfAlreadyLiked(Long userId, Long postId) {
        // This is just a dummy implementation
        return userId != null && postId != null && userId.equals(postId);
    }
}