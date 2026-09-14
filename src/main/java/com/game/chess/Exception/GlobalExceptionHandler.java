package com.game.chess.Exception;

import com.game.chess.DTO.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    //here i have handled custom exception
    @ExceptionHandler(UserNameAlreadyInUseException.class)
    public ResponseEntity<ErrorResponse> handleUsernameAlreadyInUseException(UserNameAlreadyInUseException ex, HttpServletRequest request){
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "USERNAME_EXIST", ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

}
