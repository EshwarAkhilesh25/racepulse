package com.racepulse.exception;

import com.racepulse.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFoundException ex) {
    log.warn("Resource not found: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
      .body(ApiResponse.error(ex.getMessage()));
  }

  @ExceptionHandler(ExternalApiException.class)
  public ResponseEntity<ApiResponse<Void>> handleExternalApiError(ExternalApiException ex) {
    log.error("External API failure: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
      .body(ApiResponse.error("Upstream motorsport service temporarily unavailable: " + ex.getMessage()));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
    log.warn("Invalid request parameters: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
      .body(ApiResponse.error(ex.getMessage()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception ex) {
    log.error("Unhandled internal server error", ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
      .body(ApiResponse.error("An unexpected error occurred. Please try again later."));
  }
}
