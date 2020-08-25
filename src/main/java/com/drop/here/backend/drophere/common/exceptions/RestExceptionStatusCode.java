package com.drop.here.backend.drophere.common.exceptions;

public enum RestExceptionStatusCode {
    INVALID_ARGUMENTS_CONSTRAINTS_EXCEPTION,
    INVALID_ARGUMENTS_FOR_TRANSFER_TYPE_EXCEPTION,
    INVALID_BODY_ARGUMENT_ACCOUNT_CREATION_ACCOUNT_TYPE,
    INVALID_BODY_ARGUMENT_ACCOUNT_CREATION_PASSWORD_LENGTH,
    INVALID_BODY_ARGUMENT_ACCOUNT_CREATION_MAIL_EXISTS,
    LOGIN_ACTIVE_USER_NOT_FOUND,
    LOGIN_INVALID_PASSWORD,
    LOGIN_ACTIVE_PROFILE_NOT_FOUND,
    LOGIN_INVALID_PROFILE_PASSWORD,
    JWT_AUTHENTICATION_NOT_FOUND_ACTIVE_ACCOUNT,
    JWT_AUTHENTICATION_NOT_FOUND_ACTIVE_PROFILE,
    INVALID_BODY_ARGUMENT_ACCOUNT_PROFILE_CREATION_PASSWORD_LENGTH,
    INVALID_ACCOUNT_TYPE_ACCOUNT_PROFILE_CREATION,
    PRODUCT_NOT_FOUND,
    PRODUCT_DELETE_NOT_DELETABLE,
    PRODUCT_INVALID_AVAILABILITY_STATUS,
    PRODUCT_CATEGORY_NOT_FOUND_BY_NAME,
    PRODUCT_UNIT_NOT_FOUND_BY_NAME,
    PRODUCT_CUSTOMIZATION_NOT_FOUND,
    PRODUCT_CUSTOMIZATION_INVALID_WRAPPER_TYPE
}
