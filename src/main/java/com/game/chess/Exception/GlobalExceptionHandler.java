package com.game.chess.Exception;

import com.game.chess.DTO.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    //here i have handled custom exception
    @ExceptionHandler(UserNameAlreadyInUseException.class)
    public ResponseEntity<ErrorResponse> handleUsernameAlreadyInUseException(UserNameAlreadyInUseException ex, HttpServletRequest request){
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "USERNAME_EXIST", ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(UserNotFound.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFound ex, HttpServletRequest req){
        ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(), "USER_NOT_FOUND", ex.getMessage(), req.getRequestURI());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
    //here i have handled dto level validation
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest req){
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fieldError -> fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage()));
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "VALIDATION_FAILD", ex.getMessage(), req.getRequestURI(), fieldErrors);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex, HttpServletRequest request){
        ErrorResponse error = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "INVALID_CREDENTIALS", "PLEASE ENTER VALID CREDENTIALS", request.getRequestURI());
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }
    //handled wrong method exception
    @ExceptionHandler(org.springframework.web.HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(org.springframework.web.HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                "METHOD_NOT_ALLOWED",
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.METHOD_NOT_ALLOWED);
    }
    //handle username already exists
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException ex, HttpServletRequest req){
        ErrorResponse error = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                "Username_Already_Exists",
                ex.getMessage(),
                req.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    //here i have handled path variable validation exception
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String propertyPath = violation.getPropertyPath().toString();
            String paramName = propertyPath.contains(".")
                    ? propertyPath.substring(propertyPath.lastIndexOf('.') + 1)
                    : propertyPath;

            errors.put(paramName, violation.getMessage());
        });

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "INVALID_PATH_VARIABLE",
                "One or more path variables are invalid",
                request.getRequestURI(),
                errors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    //fallback handler for all unexpected Exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception ex, HttpServletRequest req){
        log.error("Unhandled internal error at path: {}", req.getRequestURI(), ex);
        ErrorResponse error = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "UNEXPECTED_Error", "An unexpected error occurred. Please contact support if the issue persists", req.getRequestURI());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
