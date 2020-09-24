package com.drop.here.backend.drophere.authentication.account.service;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.authentication.account.entity.Privilege;
import com.drop.here.backend.drophere.authentication.account.enums.AccountProfileType;
import com.drop.here.backend.drophere.authentication.account.enums.AccountType;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PrivilegeService {
    public static final String NEW_ACCOUNT_CREATE_CUSTOMER_PRIVILEGE = "CREATE_CUSTOMER";
    public static final String OWN_PROFILE_MANAGEMENT_PRIVILEGE = "OWN_PROFILE_MANAGEMENT";
    public static final String COMPANY_FULL_MANAGEMENT_PRIVILEGE = "COMPANY_FULL_MANAGEMENT";
    public static final String COMPANY_BASIC_MANAGEMENT_PRIVILEGE = "COMPANY_BASIC_MANAGEMENT";
    public static final String COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE = "COMPANY_RESOURCES_MANAGEMENT";
    public static final String CUSTOMER_CREATED_PRIVILEGE = "CUSTOMER_FULL_MANAGEMENT";

    public void addNewAccountPrivileges(Account account) {
        addPrivilege(account, getPrivilegeName(account.getAccountType()));
    }

    private String getPrivilegeName(AccountType accountType) {
        return accountType == AccountType.COMPANY
                ? OWN_PROFILE_MANAGEMENT_PRIVILEGE
                : NEW_ACCOUNT_CREATE_CUSTOMER_PRIVILEGE;
    }

    public void addNewAccountProfilePrivileges(AccountProfile accountProfile) {
        accountProfile.setPrivileges(ListUtils.union(accountProfile.getPrivileges(), List.of(Privilege.builder()
                .name(getPrivilegeName(accountProfile.getProfileType()))
                .build())));
    }

    private String getPrivilegeName(AccountProfileType profileType) {
        return profileType == AccountProfileType.MAIN
                ? COMPANY_FULL_MANAGEMENT_PRIVILEGE
                : COMPANY_BASIC_MANAGEMENT_PRIVILEGE;
    }

    public void addCustomerCreatedPrivilege(Account account) {
        addPrivilege(account, CUSTOMER_CREATED_PRIVILEGE);
    }

    private void addPrivilege(Account account, String privilegeName) {
        final Privilege privilege = Privilege.builder()
                .name(privilegeName)
                .build();
        account.setPrivileges(ListUtils.union(account.getPrivileges(), List.of(privilege)));
    }

    public void addCompanyCreatedPrivilege(Account account) {
        addPrivilege(account, COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE);
    }
}
