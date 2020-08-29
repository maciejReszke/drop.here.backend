package com.drop.here.backend.drophere.authentication.authentication.service.base;

import com.drop.here.backend.drophere.authentication.account.dto.AuthenticationResponse;
import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.authentication.account.enums.AccountType;
import com.drop.here.backend.drophere.authentication.account.service.AccountProfileService;
import com.drop.here.backend.drophere.authentication.account.service.AccountService;
import com.drop.here.backend.drophere.authentication.authentication.dto.ExternalAuthenticationResult;
import com.drop.here.backend.drophere.authentication.authentication.dto.request.BaseLoginRequest;
import com.drop.here.backend.drophere.authentication.authentication.dto.request.ExternalAuthenticationProviderLoginRequest;
import com.drop.here.backend.drophere.authentication.authentication.dto.request.ProfileLoginRequest;
import com.drop.here.backend.drophere.authentication.authentication.dto.response.LoginResponse;
import com.drop.here.backend.drophere.authentication.authentication.exception.UnauthorizedRestException;
import com.drop.here.backend.drophere.authentication.authentication.service.external_provider.ExternalAuthenticationDelegationService;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.customer.CustomerService;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final AccountService accountService;
    private final AuthenticationExecutiveService authenticationExecutiveService;
    private final AccountProfileService accountProfileService;
    private final ExternalAuthenticationDelegationService externalAuthenticationDelegationService;
    private final CustomerService customerService;

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

    // TODO: 29/08/2020 test, implement
    @Transactional
    public LoginResponse loginWithAuthenticationProvider(ExternalAuthenticationProviderLoginRequest request) {
        final ExternalAuthenticationResult result = externalAuthenticationDelegationService.authenticate(request);

        final Account account = getOrCreateAccount(request, result);

        createCustomer(account, result);

        return authenticationExecutiveService.successLogin(account);
    }

    private Account getOrCreateAccount(ExternalAuthenticationProviderLoginRequest request, ExternalAuthenticationResult result) {
        return accountService.existsByMail(result.getEmail())
                ? getCustomerAccount(request, result)
                : accountService.createAccount(result);
    }

    private Account getCustomerAccount(ExternalAuthenticationProviderLoginRequest request, ExternalAuthenticationResult result) {
        return accountService.findActiveAccountByMail(result.getEmail())
                .filter(account -> account.getAccountType() == AccountType.CUSTOMER)
                .orElseThrow(() -> new UnauthorizedRestException(String.format("During login account via %s mail %s was not active or not a customer account", request.getProvider(), result.getEmail()),
                        RestExceptionStatusCode.LOGIN_PROVIDER_CUSTOMER_ACCOUNT_NOT_ACTIVE));
    }

    private void createCustomer(Account account, ExternalAuthenticationResult result) {
        if (account.getCustomer() == null) {
            customerService.createCustomer(account, result);
        }
    }
}
