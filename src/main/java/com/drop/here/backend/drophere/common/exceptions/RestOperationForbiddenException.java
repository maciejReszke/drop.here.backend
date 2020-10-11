package com.drop.here.backend.drophere.common.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ResponseStatus(HttpStatus.FORBIDDEN)
public class RestOperationForbiddenException extends RestException {

    public RestOperationForbiddenException(String message, RestExceptionStatusCode exceptionStatusCode) {
        super(message, HttpStatus.FORBIDDEN, exceptionStatusCode);
    }

    @Override
    public void logMessage() {
        log.info(getMessage());
    }
}
