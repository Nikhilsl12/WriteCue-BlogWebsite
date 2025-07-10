package org.coderscrib.blogapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the application.
 * This class handles exceptions thrown by controllers and converts them to appropriate HTTP responses.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles ResourceNotFoundException and returns a 404 Not Found response.
     *
     * @param ex the exception
     * @param request the web request
     * @return a ResponseEntity with status 404 and error details
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        System.out.println("[DEBUG_LOG] Handling ResourceNotFoundException: " + ex.getMessage());
        return createErrorResponse(ex, HttpStatus.NOT_FOUND, request);
    }

    /**
     * Handles BadRequestException and returns a 400 Bad Request response.
     *
     * @param ex the exception
     * @param request the web request
     * @return a ResponseEntity with status 400 and error details
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequestException(
            BadRequestException ex, WebRequest request) {
        System.out.println("[DEBUG_LOG] Handling BadRequestException: " + ex.getMessage());
        return createErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

    /**
     * Handles ConflictException and returns a 409 Conflict response.
     *
     * @param ex the exception
     * @param request the web request
     * @return a ResponseEntity with status 409 and error details
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Object> handleConflictException(
            ConflictException ex, WebRequest request) {
        System.out.println("[DEBUG_LOG] Handling ConflictException: " + ex.getMessage());
        return createErrorResponse(ex, HttpStatus.CONFLICT, request);
    }

    /**
     * Handles all other BlogAppExceptions and returns a 500 Internal Server Error response.
     *
     * @param ex the exception
     * @param request the web request
     * @return a ResponseEntity with status 500 and error details
     */
    @ExceptionHandler(BlogAppException.class)
    public ResponseEntity<Object> handleBlogAppException(
            BlogAppException ex, WebRequest request) {
        System.out.println("[DEBUG_LOG] Handling BlogAppException: " + ex.getMessage());
        return createErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    /**
     * Handles all other exceptions and returns a 500 Internal Server Error response.
     *
     * @param ex the exception
     * @param request the web request
     * @return a ResponseEntity with status 500 and error details
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(
            Exception ex, WebRequest request) {
        System.out.println("[DEBUG_LOG] Handling general Exception: " + ex.getClass().getName() + ": " + ex.getMessage());
        ex.printStackTrace(); // Print stack trace for debugging
        return createErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    /**
     * Creates an error response with the given status and exception details.
     *
     * @param ex the exception
     * @param status the HTTP status
     * @param request the web request
     * @return a ResponseEntity with the given status and error details
     */
    private ResponseEntity<Object> createErrorResponse(
            Exception ex, HttpStatus status, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(body, status);
    }
}
