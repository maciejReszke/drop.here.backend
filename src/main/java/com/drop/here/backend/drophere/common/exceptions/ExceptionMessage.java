package com.drop.here.backend.drophere.common.exceptions;

import lombok.Builder;
import lombok.Value;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Value
@Builder
public class ExceptionMessage {
    String message;

    HttpStatus status;

    LocalDateTime timestamp;

    int code;

    public String getHttpErrorMessage() {
        return status == null ? null : status.getReasonPhrase();
    }

}
