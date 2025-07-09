package org.coderscrib.blogapp.exception;

/**
 * Exception thrown when a request conflicts with the current state of the server.
 * This exception is typically used for business rule violations or when
 * an operation cannot be performed due to the current state of the resource.
 */
public class ConflictException extends BlogAppException {
    
    /**
     * Constructs a new ConflictException with null as its detail message.
     */
    public ConflictException() {
        super();
    }
    
    /**
     * Constructs a new ConflictException with the specified detail message.
     *
     * @param message the detail message
     */
    public ConflictException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new ConflictException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}