package com.drop.here.backend.drophere.authentication.account.enums;

public enum AccountType {
    COMPANY,
    CUSTOMER;

    public static AccountType parseIgnoreCase(String accountType) {
        return AccountType.valueOf(accountType.toUpperCase());
    }
}
