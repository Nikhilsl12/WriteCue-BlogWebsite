package org.coderscrib.blogapp.exception;

/**
 * Exception thrown when a requested resource cannot be found.
 * This exception is typically used when a database query returns no results
 * for a given identifier.
 */
public class ResourceNotFoundException extends BlogAppException {

    /**
     * Constructs a new ResourceNotFoundException with null as its detail message.
     */
    public ResourceNotFoundException() {
        super();
    }

    /**
     * Constructs a new ResourceNotFoundException with the specified detail message.
     *
     * @param message the detail message
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new ResourceNotFoundException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new ResourceNotFoundException with the specified cause.
     *
     * @param cause the cause
     */
    public ResourceNotFoundException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new ResourceNotFoundException with a formatted message.
     *
     * @param resourceName the name of the resource that was not found (e.g., "User", "Post")
     * @param fieldName the name of the field used in the search (e.g., "id", "email")
     * @param fieldValue the value of the field used in the search
     * @return a new ResourceNotFoundException with a formatted message
     */
    public static ResourceNotFoundException create(String resourceName, String fieldName, Object fieldValue) {
        return new ResourceNotFoundException(
                String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue));
    }
}
