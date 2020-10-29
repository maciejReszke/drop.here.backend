package com.drop.here.backend.drophere.common.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class RestException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final int httpCode;
    private final int code;

    public RestException(String message, HttpStatus httpStatus, RestExceptionStatusCode exceptionStatusCode) {
        super(message);
        this.httpStatus = httpStatus;
        this.httpCode = httpStatus.value();
        this.code = exceptionStatusCode.ordinal();
    }

    public RestException(String message, int httpCode, RestExceptionStatusCode exceptionStatusCode) {
        super(message);
        this.httpStatus = null;
        this.httpCode = httpCode;
        this.code = exceptionStatusCode.ordinal();
    }

    public abstract void logMessage();
}
