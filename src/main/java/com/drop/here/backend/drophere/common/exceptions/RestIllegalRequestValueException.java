package com.drop.here.backend.drophere.common.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
@Slf4j
public class RestIllegalRequestValueException extends RestException {

    private static final HttpStatus STATUS = HttpStatus.UNPROCESSABLE_ENTITY;

    public RestIllegalRequestValueException(String message, RestExceptionStatusCode exceptionStatusCode) {
        super(message, STATUS, exceptionStatusCode);
    }

    @Override
    public void logMessage() {
        log.info(getMessage());
    }
}
