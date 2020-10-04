package com.drop.here.backend.drophere.authentication.account.service;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.authentication.account.entity.Privilege;
import com.drop.here.backend.drophere.authentication.account.enums.AccountProfileType;
import com.drop.here.backend.drophere.authentication.account.enums.AccountType;
import com.drop.here.backend.drophere.authentication.account.repository.PrivilegeRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrivilegeService {
    private final PrivilegeRepository privilegeRepository;

    public static final String NEW_ACCOUNT_CREATE_CUSTOMER_PRIVILEGE = "CREATE_CUSTOMER";
    public static final String OWN_PROFILE_MANAGEMENT_PRIVILEGE = "OWN_PROFILE_MANAGEMENT";
    public static final String COMPANY_FULL_MANAGEMENT_PRIVILEGE = "COMPANY_FULL_MANAGEMENT";
    public static final String COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE = "COMPANY_RESOURCES_MANAGEMENT";
    public static final String CUSTOMER_CREATED_PRIVILEGE = "CUSTOMER_FULL_MANAGEMENT";
    public static final String LOGGED_ON_ANY_PROFILE_COMPANY = "COMPANY_LOGGED_ON_ANY_PROFILE";

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
        final LinkedList<Privilege> privileges = new LinkedList<>();

        if (accountProfile.getProfileType() == AccountProfileType.MAIN) {
            privileges.add(privilegeRepository.save(Privilege.builder()
                    .name(COMPANY_FULL_MANAGEMENT_PRIVILEGE)
                    .accountProfile(accountProfile)
                    .build()));
        }

        privileges.add(privilegeRepository.save(Privilege.builder()
                .name(LOGGED_ON_ANY_PROFILE_COMPANY)
                .accountProfile(accountProfile)
                .build()));

        accountProfile.setPrivileges(privileges);
    }

    @Transactional
    public void addCustomerCreatedPrivilege(Account account) {
        addPrivilege(account, CUSTOMER_CREATED_PRIVILEGE);
    }

    private void addPrivilege(Account account, String customerCreatedPrivilege) {
        final Privilege privilege = privilegeRepository.save(Privilege.builder()
                .name(customerCreatedPrivilege)
                .account(account)
                .build());
        account.setPrivileges(account.getPrivileges() == null
                ? List.of(privilege) :
                ListUtils.union(account.getPrivileges(), List.of(privilege)));
    }

    public void addCompanyCreatedPrivilege(Account account) {
        addPrivilege(account, COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE);
    }
}
