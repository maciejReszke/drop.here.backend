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
    PRODUCT_INVALID_AVAILABILITY_STATUS,
    PRODUCT_UNIT_NOT_FOUND_BY_NAME,
    PRODUCT_CUSTOMIZATION_NOT_FOUND,
    PRODUCT_CUSTOMIZATION_INVALID_WRAPPER_TYPE,
    DROP_NOT_FOUND_BY_ID,
    DROP_WITH_PASSWORD_REQUIREMENT_LACK_OF_PASSWORD,
    DROP_NOT_FOUND_BY_UID,
    LOGIN_PROVIDER_CUSTOMER_ACCOUNT_NOT_ACTIVE,
    LOGIN_PROVIDER_INVALID_LOGIN_PROVIDER,
    LOGIN_PROVIDER_INVALID_PROVIDER_REQUEST,
    LOGIN_FACEBOOK_EXCHANGE_TOKEN_FAILURE,
    LOGIN_FACEBOOK_FETCH_DATA_FAILURE,
    ACTIVE_COUNTRY_NOT_FOUND,
    INVALID_COMPANY_VISIBILITY_STATUS,
    UPDATE_COMPANY_IMAGE_INVALID_IMAGE,
    UPDATE_CUSTOMER_IMAGE_INVALID_IMAGE,
    COMPANY_IMAGE_WAS_NOT_FOUND,
    CUSTOMER_IMAGE_WAS_NOT_FOUND,
    DROP_MEMBERSHIP_BY_DROP_AND_CUSTOMER_NOT_FOUND,
    DROP_MEMBERSHIP_INVALID_PASSWORD,
    DROP_MEMBERSHIP_CUSTOMER_ALREADY_JOINED_DROP,
    UPDATE_MEMBERSHIP_BY_COMPANY_INVALID_MEMBERSHIP_STATUS,
    DROP_MEMBERSHIP_DELETE_ATTEMPT_TO_DELETE_BLOCKED_MEMBERSHIP,
    CUSTOMER_BY_ID_NOT_FOUND,
    COMPANY_BY_UID_NOT_FOUND,
    NOTIFICATION_BY_ID_FOR_PRINCIPAL_NOT_FOUND,
    NOTIFICATION_UPDATE_INVALID_READ_STATUS,
    CREATE_NOTIFICATION_TOKEN_INVALID_BROADCASTING_TYPE,
    SCHEDULE_TEMPLATE_BY_ID_AND_COMPANY_NOT_FOUND,
    PRODUCT_FRACTION_VALUE_NOT_INTEGER
}
