package com.drop.here.backend.drophere.authentication.authentication;

import com.drop.here.backend.drophere.authentication.account.enums.AccountType;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationPrivilegesService {

    public boolean isOwnAccountOperation(AccountAuthentication accountAuthentication, Long accountId) {
        return accountAuthentication.getPrincipal().getId().equals(accountId);
    }

    public boolean isOwnProfileOperation(AccountAuthentication accountAuthentication, String profileUid) {
        return accountAuthentication.hasProfile() &&
                accountAuthentication.getProfile().getProfileUid().equalsIgnoreCase(profileUid);
    }

    public boolean isOwnCompanyOperation(AccountAuthentication accountAuthentication, String companyUid) {
        return accountAuthentication.getPrincipal().getAccountType() == AccountType.COMPANY &&
                accountAuthentication.getCompany() != null &&
                accountAuthentication.getCompany().getUid().equalsIgnoreCase(companyUid);
    }
}
