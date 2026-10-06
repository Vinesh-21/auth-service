package com.energyplatform.auth_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<Map<String, Object>> handleIllegalArgumentException(
            IllegalArgumentException ex
    ) {
        return Mono.just(
                Map.of(
                        "status", HttpStatus.BAD_REQUEST.value(),
                        "error", "Bad Request",
                        "message", ex.getMessage()
                )
        );
    }

    @ExceptionHandler(Exception.class)
    public Mono<Map<String, Object>> handleException(
            IllegalArgumentException ex
    ) {
        return Mono.just(
                Map.of(
                        "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "error", "Something Went Wrong",
                        "message", ex.getMessage()
                )
        );
    }
}