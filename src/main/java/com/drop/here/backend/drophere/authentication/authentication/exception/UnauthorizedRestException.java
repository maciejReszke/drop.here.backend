package com.drop.here.backend.drophere.authentication.authentication.exception;

import com.drop.here.backend.drophere.common.exceptions.RestException;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedRestException extends RestException {
    private static final HttpStatus STATUS = HttpStatus.UNAUTHORIZED;
    private static final String USER_MESSAGE = "Unauthorized";

    private final String logMessage;

    public UnauthorizedRestException(String message, RestExceptionStatusCode statusCode) {
        super(USER_MESSAGE, STATUS, statusCode);
        this.logMessage = message;
    }

    @Override
    public void logMessage() {
        log.info(logMessage);
    }
}
