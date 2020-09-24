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
import com.drop.here.backend.drophere.configuration.security.AccountAuthentication;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final AccountService accountService;
    private final AuthenticationExecutiveService authenticationExecutiveService;
    private final AccountProfileService accountProfileService;
    private final ExternalAuthenticationDelegationService externalAuthenticationDelegationService;
    private final CustomerService customerService;

    public Mono<LoginResponse> login(BaseLoginRequest loginRequest) {
        return accountService.findActiveAccountByMail(loginRequest.getMail())
                .switchIfEmpty(Mono.error(() -> new UnauthorizedRestException("During login account was not found", RestExceptionStatusCode.LOGIN_ACTIVE_USER_NOT_FOUND)))
                .flatMap(account -> validateAccountPassword(account, loginRequest))
                .map(authenticationExecutiveService::successLogin);
    }

    private Mono<Account> validateAccountPassword(Account account, BaseLoginRequest loginRequest) {
        return accountService.isPasswordValid(account, loginRequest.getPassword())
                ? Mono.just(account)
                : Mono.error(() -> new UnauthorizedRestException(String.format("During login account %s gave invalid password", account.getId()),
                RestExceptionStatusCode.LOGIN_INVALID_PASSWORD));
    }

    public Mono<AuthenticationResponse> getAuthenticationInfo(AccountAuthentication accountAuthentication) {
        return authenticationExecutiveService.getAuthenticationInfo(accountAuthentication);
    }

    public Mono<LoginResponse> loginOnProfile(ProfileLoginRequest loginRequest, AccountAuthentication accountAuthentication) {
        final Account account = accountAuthentication.getPrincipal();

        return accountProfileService.findActiveProfile(account, loginRequest.getProfileUid())
                .switchIfEmpty(Mono.error(() -> new UnauthorizedRestException(String.format("During login on profile with account id %s and profile %s was not found",
                        account.getId(), loginRequest.getProfileUid()), RestExceptionStatusCode.LOGIN_ACTIVE_PROFILE_NOT_FOUND)))
                .flatMap(accountProfile -> validateAccountProfilePassword(accountProfile, account, loginRequest))
                .map(accountProfile -> authenticationExecutiveService.successLogin(account, accountProfile));
    }

    private Mono<AccountProfile> validateAccountProfilePassword(AccountProfile profile, Account account, ProfileLoginRequest loginRequest) {
        return accountProfileService.isPasswordValid(profile, loginRequest.getPassword())
                ? Mono.just(profile)
                : Mono.error(() -> new UnauthorizedRestException(String.format("During login profile with account id %s and profile %s gave invalid password",
                account.getId(), profile.getProfileUid()),
                RestExceptionStatusCode.LOGIN_INVALID_PROFILE_PASSWORD));
    }

    // TODO: 23/09/2020 transactional
    public Mono<LoginResponse> loginWithAuthenticationProvider(ExternalAuthenticationProviderLoginRequest request) {
        return externalAuthenticationDelegationService.authenticate(request)
                .flatMap(result -> getOrCreateAccount(request, result)
                        .flatMap(account -> createCustomer(account, result)
                                .map(customer -> authenticationExecutiveService.successLogin(account))));
    }

    private Mono<Account> getOrCreateAccount(ExternalAuthenticationProviderLoginRequest request, ExternalAuthenticationResult result) {
        return accountService.existsByMail(result.getEmail())
                .flatMap(ignore -> getCustomerAccount(request, result))
                .switchIfEmpty(Mono.defer(() -> accountService.createAccount(result)));
    }

    private Mono<Account> getCustomerAccount(ExternalAuthenticationProviderLoginRequest request, ExternalAuthenticationResult result) {
        return accountService.findActiveAccountByMail(result.getEmail())
                .filter(account -> account.getAccountType() == AccountType.CUSTOMER)
                .switchIfEmpty(Mono.error(() -> new UnauthorizedRestException(String.format("During login account via %s was not active or not a customer account", request.getProvider()),
                        RestExceptionStatusCode.LOGIN_PROVIDER_CUSTOMER_ACCOUNT_NOT_ACTIVE)));
    }

    private Mono<Customer> createCustomer(Account account, ExternalAuthenticationResult result) {
        return account.getCustomer() == null
                ? customerService.createCustomer(account, result)
                : Mono.just(account.getCustomer());
    }
}
