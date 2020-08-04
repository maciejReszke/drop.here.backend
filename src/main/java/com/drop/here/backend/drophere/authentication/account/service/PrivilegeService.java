package com.drop.here.backend.drophere.authentication.account.service;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.Privilege;
import com.drop.here.backend.drophere.authentication.account.enums.AccountType;
import com.drop.here.backend.drophere.authentication.account.repository.PrivilegeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PrivilegeService {
    private final PrivilegeRepository privilegeRepository;

    public static final String NEW_ACCOUNT_CREATE_COMPANY_PRIVILEGE = "CREATE_COMPANY";
    public static final String NEW_ACCOUNT_CREATE_CUSTOMER_PRIVILEGE = "CREATE_CUSTOMER";

    public void addNewAccountPrivileges(Account account) {
        final Privilege privilege = privilegeRepository.save(Privilege.builder()
                .name(getPrivilegeName(account.getAccountType()))
                .account(account)
                .build());
        account.setPrivileges(List.of(privilege));
    }

    private String getPrivilegeName(AccountType accountType) {
        return accountType == AccountType.COMPANY
                ? NEW_ACCOUNT_CREATE_COMPANY_PRIVILEGE
                : NEW_ACCOUNT_CREATE_CUSTOMER_PRIVILEGE;
    }
}
