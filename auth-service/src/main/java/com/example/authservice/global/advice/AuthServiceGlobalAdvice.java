package com.example.authservice.global.advice;

import com.example.authservice.global.exceptions.auth.UserAuthException;
import com.example.authservice.global.exceptions.token.JwtTokenException;
import com.example.authservice.global.response.ApiError;
import io.jsonwebtoken.ExpiredJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.security.NoSuchAlgorithmException;

@RestControllerAdvice
public class AuthServiceGlobalAdvice {

    private final Logger logger =
            LoggerFactory.getLogger(AuthServiceGlobalAdvice.class);

    @ExceptionHandler(NoSuchAlgorithmException.class)
    public ResponseEntity<ApiError> noSuchAlgorithmException(NoSuchAlgorithmException ex) {
        logger.error("Failed to hash token: {}", ex.getMessage());
        return ResponseEntity.internalServerError().body(
                ApiError.of(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                        "Failed to load the hash algorithm",
                        null)
        );
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ApiError> expiredJwtException(ExpiredJwtException ex) {
        logger.error("Expired JWT token: {}", ex.getMessage());
        return ResponseEntity.internalServerError().body(
                ApiError.of(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                        "Expired token supplied",
                        null)
        );
    }

    @ExceptionHandler(JwtTokenException.class)
    public ResponseEntity<ApiError> jwtTokenException(JwtTokenException ex) {
        logger.error("Exception in token processing: {}", ex.getMessage());
        return ResponseEntity.internalServerError().body(
                ApiError.of(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                        ex.getMessage(),
                        null)
        );
    }

    @ExceptionHandler(UserAuthException.class)
    public ResponseEntity<ApiError> userAuthException(UserAuthException ex) {
        logger.error("Exception in User details processing: {}", ex.getMessage());
        return ResponseEntity.internalServerError().body(
                ApiError.of(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                        ex.getMessage(),
                        null)
        );
    }
}
