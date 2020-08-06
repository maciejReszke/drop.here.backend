package com.drop.here.backend.drophere.authentication.authentication;

import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationPrivilegesService {

    public boolean isOwnAccountOperation(AccountAuthentication accountAuthentication, Long accountId) {
        return accountAuthentication.getPrincipal().getId().equals(accountId);
    }

    public boolean isOwnProfileOperation(AccountAuthentication accountAuthentication, String profileUid) {
        return accountAuthentication.getProfile() != null &&
                accountAuthentication.getProfile().getProfileUid().equalsIgnoreCase(profileUid);
    }
}
