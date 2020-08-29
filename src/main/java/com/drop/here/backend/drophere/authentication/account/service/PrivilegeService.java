package com.drop.here.backend.drophere.authentication.account.service;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.authentication.account.entity.Privilege;
import com.drop.here.backend.drophere.authentication.account.enums.AccountProfileType;
import com.drop.here.backend.drophere.authentication.account.enums.AccountType;
import com.drop.here.backend.drophere.authentication.account.repository.PrivilegeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PrivilegeService {
    private final PrivilegeRepository privilegeRepository;

    public static final String NEW_ACCOUNT_CREATE_CUSTOMER_PRIVILEGE = "CREATE_CUSTOMER";
    public static final String OWN_PROFILE_MANAGEMENT_PRIVILEGE = "OWN_PROFILE_MANAGEMENT";
    public static final String COMPANY_FULL_MANAGEMENT_PRIVILEGE = "COMPANY_FULL_MANAGEMENT";
    public static final String COMPANY_BASIC_MANAGEMENT_PRIVILEGE = "COMPANY_BASIC_MANAGEMENT";
    public static final String COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE = "COMPANY_RESOURCES_MANAGEMENT";

    public void addNewAccountPrivileges(Account account) {
        final Privilege privilege = privilegeRepository.save(Privilege.builder()
                .name(getPrivilegeName(account.getAccountType()))
                .account(account)
                .build());
        account.setPrivileges(List.of(privilege));
    }

    private String getPrivilegeName(AccountType accountType) {
        return accountType == AccountType.COMPANY
                ? OWN_PROFILE_MANAGEMENT_PRIVILEGE
                : NEW_ACCOUNT_CREATE_CUSTOMER_PRIVILEGE;
    }

    public void addNewAccountProfilePrivileges(AccountProfile accountProfile) {
        final Privilege privilege = privilegeRepository.save(Privilege.builder()
                .name(getPrivilegeName(accountProfile.getProfileType()))
                .accountProfile(accountProfile)
                .build());
        accountProfile.setPrivileges(List.of(privilege));
    }

    private String getPrivilegeName(AccountProfileType profileType) {
        return profileType == AccountProfileType.MAIN
                ? COMPANY_FULL_MANAGEMENT_PRIVILEGE
                : COMPANY_BASIC_MANAGEMENT_PRIVILEGE;
    }
}
