package com.drop.here.backend.drophere.authentication.authentication;

import com.drop.here.backend.drophere.authentication.account.dto.AuthenticationResponse;
import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.authentication.account.service.AccountProfileService;
import com.drop.here.backend.drophere.authentication.account.service.AccountService;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AccountService accountService;
    private final AuthenticationExecutiveService authenticationExecutiveService;
    private final AccountProfileService accountProfileService;

    public LoginResponse login(BaseLoginRequest loginRequest) {
        final Account account = accountService.findActiveAccountByMail(loginRequest.getMail())
                .orElseThrow(() -> new UnauthorizedRestException(String.format("During login account with mail %s was not found", loginRequest.getMail()),
                        RestExceptionStatusCode.LOGIN_ACTIVE_USER_NOT_FOUND));

        if (!accountService.isPasswordValid(account, loginRequest.getPassword())) {
            throw new UnauthorizedRestException(String.format("During login account with mail %s gave invalid password", loginRequest.getMail()),
                    RestExceptionStatusCode.LOGIN_INVALID_PASSWORD);
        }

        return authenticationExecutiveService.successLogin(account);
    }

    public AuthenticationResponse getAuthenticationInfo(AccountAuthentication accountAuthentication) {
        return authenticationExecutiveService.getAuthenticationInfo(accountAuthentication);
    }

    public LoginResponse loginOnProfile(ProfileLoginRequest loginRequest, AccountAuthentication accountAuthentication) {
        final Account account = accountAuthentication.getPrincipal();

        final AccountProfile profile = accountProfileService.findActiveByAccountAndProfileUidWithRoles(account, loginRequest.getProfileUid())
                .orElseThrow(() -> new UnauthorizedRestException(String.format("During login on profile with account id %s and profile %s was not found",
                        account.getId(), loginRequest.getProfileUid()), RestExceptionStatusCode.LOGIN_ACTIVE_PROFILE_NOT_FOUND));

        if (!accountProfileService.isPasswordValid(profile, loginRequest.getPassword())) {
            throw new UnauthorizedRestException(String.format("During login profile with account id %s and profile %s gave invalid password",
                    account.getId(), profile.getProfileUid()),
                    RestExceptionStatusCode.LOGIN_INVALID_PROFILE_PASSWORD);
        }

        return authenticationExecutiveService.successLogin(account, profile);
    }
}
