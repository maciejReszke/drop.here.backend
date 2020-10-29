package com.drop.here.backend.drophere.route.exception;

import com.drop.here.backend.drophere.common.exceptions.RestException;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NotEnoughProductsException extends RestException {

    public NotEnoughProductsException(String message) {
        super(message, 601, RestExceptionStatusCode.NOT_ENOUGH_PRODUCTS_FOR_ORDER);
    }

    @Override
    public void logMessage() {
        log.info(getMessage());
    }
}
