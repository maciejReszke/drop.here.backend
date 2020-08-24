package com.drop.here.backend.drophere.common.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ResponseStatus(HttpStatus.NOT_FOUND)
public class RestEntityNotFoundException extends RestException {

    public RestEntityNotFoundException(String message, RestExceptionStatusCode exceptionStatusCode) {
        super(message, HttpStatus.NOT_FOUND, exceptionStatusCode);
    }

    @Override
    public void logMessage() {
        log.info(getMessage());
    }
}
