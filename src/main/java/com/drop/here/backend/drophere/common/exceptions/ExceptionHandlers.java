package com.drop.here.backend.drophere.common.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class ExceptionHandlers {
    @ExceptionHandler(RestException.class)
    public ResponseEntity<ExceptionMessage> handleRestException(RestException exception) {
        exception.logMessage();
        return ResponseEntity
                .status(exception.getHttpCode())
                .body(ExceptionMessage.builder()
                        .message(exception.getMessage())
                        .status(exception.getHttpStatus())
                        .timestamp(LocalDateTime.now())
                        .code(exception.getCode())
                        .build());
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ExceptionMessage> handleException(WebExchangeBindException exception) {
        final String message = exception.getBindingResult().getFieldErrors()
                .stream()
                .map(fieldError -> String.format("Field %s has error: %s", fieldError.getField(), fieldError.getDefaultMessage()))
                .collect(Collectors.joining("\n"));
        log.info("Invalid request caused by invalid arguments {}", message);
        final HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        return ResponseEntity
                .status(status)
                .body(ExceptionMessage.builder()
                        .message(message)
                        .status(status)
                        .timestamp(LocalDateTime.now())
                        .code(RestExceptionStatusCode.INVALID_ARGUMENTS_CONSTRAINTS_EXCEPTION.ordinal())
                        .build());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionMessage> handleException(ConstraintViolationException exception) {
        final String message = exception.getConstraintViolations()
                .stream()
                .map(fieldError -> String.format("Found error: %s  %s", fieldError.getPropertyPath(), fieldError.getMessage()))
                .collect(Collectors.joining("\n"));
        log.info("Invalid request caused by invalid arguments {}", message);
        final HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        return ResponseEntity
                .status(status)
                .body(ExceptionMessage.builder()
                        .message(message)
                        .status(status)
                        .timestamp(LocalDateTime.now())
                        .code(RestExceptionStatusCode.INVALID_ARGUMENTS_FOR_TRANSFER_TYPE_EXCEPTION.ordinal())
                        .build());
    }
}
