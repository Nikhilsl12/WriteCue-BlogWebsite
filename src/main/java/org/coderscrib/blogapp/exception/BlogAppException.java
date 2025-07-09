package org.coderscrib.blogapp.exception;

/**
 * Base exception class for all application-specific exceptions.
 * This class extends RuntimeException to avoid forcing clients to catch declared exceptions.
 */
public class BlogAppException extends RuntimeException {
    
    /**
     * Constructs a new BlogAppException with null as its detail message.
     */
    public BlogAppException() {
        super();
    }
    
    /**
     * Constructs a new BlogAppException with the specified detail message.
     *
     * @param message the detail message
     */
    public BlogAppException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new BlogAppException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public BlogAppException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructs a new BlogAppException with the specified cause.
     *
     * @param cause the cause
     */
    public BlogAppException(Throwable cause) {
        super(cause);
    }
}