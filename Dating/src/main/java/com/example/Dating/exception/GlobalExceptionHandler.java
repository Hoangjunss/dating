package com.example.Dating.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Global exception handler for all REST API endpoints.
 * Ensures every exception returns a consistent ErrorResponse.
 * Handler priority order (Spring chooses the most specific handler):
 * ResourceNotFoundException → 404
 * DuplicateResourceException → 409
 * ValidationException → 400
 * EntityNotFoundException → 404
 * IllegalArgumentException → 400
 * IllegalStateException → 400
 * MissingServletRequestParam → 400
 * MethodArgumentTypeMismatch → 400
 * MethodArgumentNotValid (@Valid) → 400 + fieldErrors
 * Exception (fallback) → 500
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    // -------------------------------------------------------------------------
    // 404 NOT FOUND
    // -------------------------------------------------------------------------

    /**
     * ResourceNotFoundException — resource does not exist.
     * Thrown by: all ServiceImpl when findById has no result.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex, WebRequest request) {

        log.warn("Resource not found: {}", ex.getMessage());
        return build(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage(), request);
    }

    /**
     * EntityNotFoundException — JPA entity not found.
     * Thrown by: UserProfileServiceImpl.findEntityById().
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(
            EntityNotFoundException ex, WebRequest request) {

        log.warn("Entity not found: {}", ex.getMessage());
        return build(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage(), request);
    }

    // -------------------------------------------------------------------------
    // 409 CONFLICT
    // -------------------------------------------------------------------------

    /**
     * DuplicateResourceException — creating an already existing resource.
     * Thrown by: registering duplicate username/email, creating profile twice,
     * adding duplicate interest, delete-for-me twice.
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResource(
            DuplicateResourceException ex, WebRequest request) {

        log.warn("Duplicate resource: {}", ex.getMessage());
        return build(HttpStatus.CONFLICT, "CONFLICT", ex.getMessage(), request);
    }

    // -------------------------------------------------------------------------
    // 400 BAD REQUEST
    // -------------------------------------------------------------------------

    /**
     * ValidationException — business rule violation.
     * Thrown by: AuthServiceImpl (wrong password), MessageServiceImpl
     * (not the sender, not a member of the conversation).
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            ValidationException ex, WebRequest request) {

        log.warn("Validation error: {}", ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", ex.getMessage(), request);
    }

    /**
     * IllegalArgumentException — invalid parameter.
     * Thrown by: UserSwipeServiceImpl (swiping oneself).
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex, WebRequest request) {

        log.warn("Illegal argument: {}", ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage(), request);
    }

    /**
     * IllegalStateException — an invalid state to perform the action.
     * Thrown by: ConversationServiceImpl (conversation already exists, users have not matched),
     * UserSwipeServiceImpl (already swiped this user),
     * MessageServiceImpl (match is no longer active).
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(
            IllegalStateException ex, WebRequest request) {

        log.warn("Illegal state: {}", ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage(), request);
    }

    /**
     * MissingServletRequestParameterException — missing required @RequestParam.
     * Example: GET /api/messages/{id} without passing viewerId.
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(
            MissingServletRequestParameterException ex, WebRequest request) {

        log.warn("Missing request parameter: {}", ex.getMessage());
        String message = "Required parameter '" + ex.getParameterName() + "' is missing";
        return build(HttpStatus.BAD_REQUEST, "MISSING_PARAMETER", message, request);
    }

    /**
     * MethodArgumentTypeMismatchException — @PathVariable or @RequestParam has the wrong type.
     * Example: passing a string instead of a UUID into {userId}.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, WebRequest request) {

        log.warn("Type mismatch for parameter '{}': {}", ex.getName(), ex.getMessage());
        String message = "Invalid value for parameter '" + ex.getName() + "': expected "
                + (ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "correct type");
        return build(HttpStatus.BAD_REQUEST, "TYPE_MISMATCH", message, request);
    }

    /**
     * MethodArgumentNotValidException — @Valid fails on @RequestBody.
     * Returns a list of fieldErrors so the client knows which field is incorrect.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, WebRequest request) {

        log.warn("Request body validation failed: {} field error(s)",
                ex.getBindingResult().getErrorCount());

        List<ErrorResponse.FieldError> fieldErrors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName    = ((FieldError) error).getField();
            String message      = error.getDefaultMessage();
            Object rejectedValue = ((FieldError) error).getRejectedValue();

            fieldErrors.add(ErrorResponse.FieldError.builder()
                    .field(fieldName)
                    .message(message)
                    .rejectedValue(rejectedValue)
                    .build());
        });

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Input validation failed")
                .error("VALIDATION_ERROR")
                .timestamp(Instant.now())
                .path(extractPath(request))
                .fieldErrors(fieldErrors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // -------------------------------------------------------------------------
    // 500 INTERNAL SERVER ERROR — fallback cuối cùng
    // -------------------------------------------------------------------------

    /**
     * Catch-all for any exceptions not handled above.
     * Log the full stack trace for debugging. Client only receives a general message.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex, WebRequest request) {

        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred. Please try again later.", request);
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    private ResponseEntity<ErrorResponse> build(
            HttpStatus status, String error, String message, WebRequest request) {

        ErrorResponse body = ErrorResponse.builder()
                .status(status.value())
                .message(message)
                .error(error)
                .timestamp(Instant.now())
                .path(extractPath(request))
                .build();

        return ResponseEntity.status(status).body(body);
    }

    private String extractPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
}