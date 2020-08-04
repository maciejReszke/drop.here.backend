package com.drop.here.backend.drophere.common.exceptions;

public enum RestExceptionStatusCode {
    INVALID_ARGUMENTS_CONSTRAINTS_EXCEPTION,
    INVALID_ARGUMENTS_FOR_TRANSFER_TYPE_EXCEPTION,
    INVALID_BODY_ARGUMENT_ACCOUNT_CREATION_ACCOUNT_TYPE,
    INVALID_BODY_ARGUMENT_ACCOUNT_CREATION_PASSWORD_LENGTH,
    INVALID_BODY_ARGUMENT_ACCOUNT_CREATION_MAIL_EXISTS,
    LOGIN_ACTIVE_USER_NOT_FOUND,
    LOGIN_INVALID_PASSWORD,
    JWT_AUTHENTICATION_NOT_FOUND_ACTIVE_ACCOUNT
}