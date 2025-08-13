package org.coderscrib.blogapp.exception;

/**
 * Exception thrown when a client sends an invalid request.
 * This exception is typically used for validation errors or when
 * required parameters are missing or invalid.
 */
public class BadRequestException extends BlogAppException {

    /**
     * Constructs a new BadRequestException with null as its detail message.
     */
    public BadRequestException() {
        super();
    }

    /**
     * Constructs a new BadRequestException with the specified detail message.
     *
     * @param message the detail message
     */
    public BadRequestException(String message) {
        super(message);
    }

    /**
     * Constructs a new BadRequestException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new BadRequestException with the specified cause.
     *
     * @param cause the cause
     */
    public BadRequestException(Throwable cause) {
        super(cause);
    }
}
