package com.substring.docmind.exception;

import com.substring.docmind.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * ============================================================================================
 * IMPORTANT CONCEPT & INTERVIEW Q&A: @RestControllerAdvice
 * ============================================================================================
 * Q1: What is @RestControllerAdvice and how does it work?
 * A1: It is a specialized convenience annotation in Spring that combines:
 * 
 * @ControllerAdvice + @ResponseBody.
 *                   It acts as a global interceptor using Spring AOP
 *                   (Aspect-Oriented Programming).
 *                   Any exception thrown from any @RestController in the
 *                   application is intercepted here,
 *                   and the returned object is automatically serialized into
 *                   JSON/XML (via HttpMessageConverter).
 *
 *                   Q2: What is the difference between @ControllerAdvice
 *                   and @RestControllerAdvice?
 *                   A2: @ControllerAdvice returns view templates (like
 *                   JSP/Thymeleaf) unless methods are explicitly
 *                   annotated with @ResponseBody. @RestControllerAdvice
 *                   automatically includes @ResponseBody
 *                   on all methods, making it ideal for REST APIs.
 *
 *                   Q3: What design pattern does @RestControllerAdvice
 *                   implement?
 *                   A3: It implements the Interceptor / Cross-Cutting Concern
 *                   (AOP) pattern and the Front Controller /
 *                   Global Error Handler pattern, decoupling error handling
 *                   logic from core business logic.
 *                   ============================================================================================
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * INTERVIEW Q: Why use SLF4J Logger instead of System.out.println() or
     * e.printStackTrace()?
     * 1. Performance: System.out.println is synchronous and thread-blocking.
     * SLF4J/Logback supports asynchronous, non-blocking logging.
     * 2. Log Levels: Allows filtering by levels (TRACE, DEBUG, INFO, WARN, ERROR)
     * per environment (dev vs prod) via application.properties.
     * 3. Metadata & Observability: Automatically records timestamps, thread names,
     * class names, and integrates with cloud monitors (AWS CloudWatch, Datadog, ELK
     * stack).
     * 4. Why 'static final'? 'static' ensures a single logger instance per class
     * (conserving memory across requests); 'final' ensures thread safety and
     * immutability.
     */
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * INTERVIEW Q: How does Spring resolve which @ExceptionHandler to call?
     * A: Spring uses the "Most Specific Exception" rule. When an exception occurs,
     * Spring checks the exception hierarchy and calls the handler matching the
     * closest subclass
     * before falling back to broader handlers (like RuntimeException or
     * Exception.class).
     */

    // --------------------------------------------------------------------------------------------
    // 1. Resource Not Found Handling (HTTP 404)
    // --------------------------------------------------------------------------------------------
    /**
     * Triggered when a requested entity (e.g. Document or User) does not exist in
     * the database.
     * Logged as WARN because this is a client-side mistake (requesting an invalid
     * ID), not a server crash.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleNotFound(ResourceNotFoundException ex) {
        logger.warn("Resource not found : {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse.builder()
                        .success(false)
                        .message(ex.getMessage())
                        .data(null)
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    // --------------------------------------------------------------------------------------------
    // 2. Custom Business/Domain Exception (HTTP 422 Unprocessable Content)
    // --------------------------------------------------------------------------------------------
    /**
     * Triggered when a document fails to parse, chunk, or embed in vector store.
     * INTERVIEW Q: Why pass 'ex' as the last argument in logger.error()?
     * A: In SLF4J, passing the Throwable as the last argument automatically prints
     * the full stack trace
     * without needing a "{}" placeholder for it.
     */
    @ExceptionHandler(DocumentProcessingException.class)
    public ResponseEntity<ApiResponse<Object>> handleProcessingError(DocumentProcessingException ex) {
        logger.error("Document processing error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(
                ApiResponse.builder()
                        .success(false)
                        .message("Failed to process document: " + ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    // --------------------------------------------------------------------------------------------
    // 3. File Upload Size Exceeded Handling
    // --------------------------------------------------------------------------------------------
    /**
     * Thrown by Spring MultipartResolver when the uploaded file exceeds
     * 'spring.servlet.multipart.max-file-size'.
     * Prevents server denial-of-service (DoS) from excessively large file uploads.
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Object>> handleMaxSize(MaxUploadSizeExceededException ex) {
        logger.warn("File size limit exceeded: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(
                ApiResponse.builder()
                        .success(false)
                        .message("File size exceeds the allowed limit (25MB)")
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    // --------------------------------------------------------------------------------------------
    // 4. Bean Validation Handling (HTTP 400 Bad Request)
    // --------------------------------------------------------------------------------------------
    /**
     * ============================================================================================
     * TOP INTERVIEW QUESTION:
     * Q: How do you handle DTO validation errors (@Valid, @NotNull, @Size, @Email)
     * globally?
     * A: When validation on a @Valid @RequestBody fails, Spring throws
     * MethodArgumentNotValidException.
     * We intercept it here, extract BindingResult.getFieldErrors(), and map each
     * field name to its
     * validation error message into a Map<String, String>.
     *
     * Example Response Body:
     * {
     * "success": false,
     * "message": "Validation Failed",
     * "data": { "email": "must be a well-formed email address", "password": "size
     * must be between 6 and 20" },
     * "timestamp": "2026-10-17T..."
     * }
     * ============================================================================================
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(MethodArgumentNotValidException ex) {
        // key value pair -> fieldName: errorMessage
        Map<String, String> errors = new HashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        ApiResponse.<Map<String, String>>builder()
                                .success(false)
                                .message("Validation Failed")
                                .data(errors)
                                .timestamp(LocalDateTime.now())
                                .build());
    }

    // --------------------------------------------------------------------------------------------
    // 5. Illegal Argument Handling (HTTP 400 Bad Request)
    // --------------------------------------------------------------------------------------------
    /**
     * Handles bad inputs passed into services or methods (e.g., negative page
     * numbers, invalid enum strings).
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex) {
        logger.warn("Illegal argument: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        ApiResponse.builder()
                                .success(false)
                                .message(ex.getMessage())
                                .data(null)
                                .timestamp(LocalDateTime.now())
                                .build());
    }

    // --------------------------------------------------------------------------------------------
    // 6. Generic Fallback Exception Handling (Catch-All)
    // --------------------------------------------------------------------------------------------
    /**
     * ============================================================================================
     * TOP INTERVIEW QUESTION:
     * Q: Why is a generic @ExceptionHandler(Exception.class) required in a
     * production REST API?
     * A: 1. Safety Net: Catches any unexpected runtime exceptions
     * (NullPointerException, DB connection down, etc.)
     * so the server does not return default Tomcat/Spring HTML error white-label
     * pages.
     * 2. Security: Prevents internal SQL syntax or server directory paths from
     * leaking to the client.
     * 3. Consistent API Contract: Guarantees that every single response from the
     * API matches
     * the structured ApiResponse<T> schema.
     * ============================================================================================
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex) {
        logger.error("Unexpected error occurred: ", ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        ApiResponse.builder()
                                .success(false)
                                .message("An unexpected error occurred: " + ex.getMessage())
                                .data(null)
                                .timestamp(LocalDateTime.now())
                                .build());
    }

}
