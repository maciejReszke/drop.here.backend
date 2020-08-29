package com.drop.here.backend.drophere.authentication.authentication;

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
import com.drop.here.backend.drophere.authentication.authentication.service.base.AuthenticationExecutiveService;
import com.drop.here.backend.drophere.authentication.authentication.service.base.AuthenticationService;
import com.drop.here.backend.drophere.authentication.authentication.service.external_provider.ExternalAuthenticationDelegationService;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.company.Company;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.customer.service.CustomerService;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import com.drop.here.backend.drophere.test_data.AccountProfileDataGenerator;
import com.drop.here.backend.drophere.test_data.AuthenticationDataGenerator;
import com.drop.here.backend.drophere.test_data.ExternalAuthenticationDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @InjectMocks
    private AuthenticationService authenticationService;

    @Mock
    private AccountService accountService;

    @Mock
    private AuthenticationExecutiveService authenticationExecutiveService;

    @Mock
    private AccountProfileService accountProfileService;

    @Mock
    private ExternalAuthenticationDelegationService externalAuthenticationDelegationService;

    @Mock
    private CustomerService customerService;

    @Test
    void givenValidBaseRequestWhenLoginThenLogin() {
        //given
        final BaseLoginRequest baseLoginRequest = BaseLoginRequest.builder()
                .mail("mail")
                .password("password")
                .build();
        final Company company = Company.builder().build();
        final Account account = AccountDataGenerator.companyAccount(1, company);
        final LoginResponse response = new LoginResponse("token", "validUntil");

        when(accountService.findActiveAccountByMail(baseLoginRequest.getMail())).thenReturn(Optional.of(account));
        when(accountService.isPasswordValid(account, baseLoginRequest.getPassword())).thenReturn(true);
        when(authenticationExecutiveService.successLogin(account)).thenReturn(response);

        //when
        final LoginResponse result = authenticationService.login(baseLoginRequest);

        //then
        assertThat(result).isEqualTo(response);
    }

    @Test
    void givenNotExistingAccountWhenLoginThenError() {
        //given
        final BaseLoginRequest baseLoginRequest = BaseLoginRequest.builder()
                .mail("mail")
                .password("password")
                .build();

        when(accountService.findActiveAccountByMail(baseLoginRequest.getMail())).thenReturn(Optional.empty());

        //when
        final Throwable throwable = catchThrowable(() -> authenticationService.login(baseLoginRequest));

        //then
        assertThat(throwable).isInstanceOf(UnauthorizedRestException.class);
        assertThat(((UnauthorizedRestException) (throwable)).getCode()).isEqualTo(RestExceptionStatusCode.LOGIN_ACTIVE_USER_NOT_FOUND.ordinal());
    }

    @Test
    void givenInvalidPasswordWhenLoginThenError() {
        //given
        final BaseLoginRequest baseLoginRequest = BaseLoginRequest.builder()
                .mail("mail")
                .password("password")
                .build();
        final Company company = Company.builder().build();
        final Account account = AccountDataGenerator.companyAccount(1, company);

        when(accountService.findActiveAccountByMail(baseLoginRequest.getMail())).thenReturn(Optional.of(account));
        when(accountService.isPasswordValid(account, baseLoginRequest.getPassword())).thenReturn(false);

        //when
        final Throwable throwable = catchThrowable(() -> authenticationService.login(baseLoginRequest));

        //then
        assertThat(throwable).isInstanceOf(UnauthorizedRestException.class);
        assertThat(((UnauthorizedRestException) (throwable)).getCode()).isEqualTo(RestExceptionStatusCode.LOGIN_INVALID_PASSWORD.ordinal());
    }

    @Test
    void givenAccountAuthenticationWithoutProfileWhenGetAuthenticationInfoThenGet() {
        //given
        final Company company = Company.builder().build();
        final Account account = AccountDataGenerator.companyAccount(1, company);
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);
        final AuthenticationResponse info = AuthenticationResponse.builder().build();
        when(authenticationExecutiveService.getAuthenticationInfo(accountAuthentication)).thenReturn(info);

        //when
        final AuthenticationResponse authenticationInfo = authenticationService.getAuthenticationInfo(accountAuthentication);

        //then
        assertThat(authenticationInfo).isEqualTo(info);
    }

    @Test
    void givenValidProfileLoginRequestWhenLoginOnProfileThenLogin() {
        //given
        final ProfileLoginRequest loginRequest = ProfileLoginRequest.builder()
                .profileUid("profileUid")
                .password("password")
                .build();
        final Company company = Company.builder().build();
        final Account account = AccountDataGenerator.companyAccount(1, company);
        final LoginResponse response = new LoginResponse("token", "validUntil");
        final AccountProfile accountProfile = AccountProfileDataGenerator.accountProfile(1, account);
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);

        when(accountProfileService.findActiveByAccountAndProfileUidWithRoles(account, "profileUid")).thenReturn(Optional.of(accountProfile));
        when(accountProfileService.isPasswordValid(accountProfile, "password")).thenReturn(true);
        when(authenticationExecutiveService.successLogin(account, accountProfile)).thenReturn(response);

        //when
        final LoginResponse result = authenticationService.loginOnProfile(loginRequest, accountAuthentication);

        //then
        assertThat(result).isEqualTo(response);
    }

    @Test
    void givenNotExistingAccountProfileWhenLoginOnProfileThenError() {
        //given
        final ProfileLoginRequest loginRequest = ProfileLoginRequest.builder()
                .profileUid("profileUid")
                .password("password")
                .build();
        final Company company = Company.builder().build();
        final Account account = AccountDataGenerator.companyAccount(1, company);
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);

        when(accountProfileService.findActiveByAccountAndProfileUidWithRoles(account, "profileUid")).thenReturn(Optional.empty());

        //when
        final Throwable throwable = catchThrowable(() -> authenticationService.loginOnProfile(loginRequest, accountAuthentication));

        //then
        assertThat(throwable).isInstanceOf(UnauthorizedRestException.class);
        assertThat(((UnauthorizedRestException) (throwable)).getCode()).isEqualTo(RestExceptionStatusCode.LOGIN_ACTIVE_PROFILE_NOT_FOUND.ordinal());
    }

    @Test
    void givenInvalidPasswordWhenLoginOnProfileThenError() {
        //given
        final ProfileLoginRequest loginRequest = ProfileLoginRequest.builder()
                .profileUid("profileUid")
                .password("password")
                .build();
        final Company company = Company.builder().build();
        final Account account = AccountDataGenerator.companyAccount(1, company);
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);
        final AccountProfile accountProfile = AccountProfileDataGenerator.accountProfile(1, account);

        when(accountProfileService.findActiveByAccountAndProfileUidWithRoles(account, "profileUid")).thenReturn(Optional.of(accountProfile));
        when(accountProfileService.isPasswordValid(accountProfile, "password")).thenReturn(false);
        //when
        final Throwable throwable = catchThrowable(() -> authenticationService.loginOnProfile(loginRequest, accountAuthentication));

        //then
        assertThat(throwable).isInstanceOf(UnauthorizedRestException.class);
        assertThat(((UnauthorizedRestException) (throwable)).getCode()).isEqualTo(RestExceptionStatusCode.LOGIN_INVALID_PROFILE_PASSWORD.ordinal());
    }

    @Test
    void givenExistingCustomerAccountAndCustomerWhenLoginWithAuthenticationProviderThenLogin() {
        //given
        final ExternalAuthenticationProviderLoginRequest request = ExternalAuthenticationDataGenerator.facebook(1);
        final ExternalAuthenticationResult authenticationResult = ExternalAuthenticationDataGenerator.externalAuthenticationResult(1);
        final Account account = AccountDataGenerator.customerAccount(1);
        account.setCustomer(Customer.builder().build());
        final LoginResponse loginResponse = LoginResponse.builder().build();

        when(externalAuthenticationDelegationService.authenticate(request)).thenReturn(authenticationResult);
        when(accountService.existsByMail(authenticationResult.getEmail())).thenReturn(true);
        when(accountService.findActiveAccountByMail(authenticationResult.getEmail())).thenReturn(Optional.of(account));
        when(authenticationExecutiveService.successLogin(account)).thenReturn(loginResponse);

        //when
        final LoginResponse response = authenticationService.loginWithAuthenticationProvider(request);

        //then
        assertThat(response).isEqualTo(loginResponse);
    }

    @Test
    void givenExistingCustomerAccountAndNotCustomerWhenLoginWithAuthenticationProviderThenLoginAndCreateCustomer() {
        //given
        final ExternalAuthenticationProviderLoginRequest request = ExternalAuthenticationDataGenerator.facebook(1);
        final ExternalAuthenticationResult authenticationResult = ExternalAuthenticationDataGenerator.externalAuthenticationResult(1);
        final Account account = AccountDataGenerator.customerAccount(1);
        account.setCustomer(null);
        final LoginResponse loginResponse = LoginResponse.builder().build();

        when(externalAuthenticationDelegationService.authenticate(request)).thenReturn(authenticationResult);
        when(accountService.existsByMail(authenticationResult.getEmail())).thenReturn(true);
        when(accountService.findActiveAccountByMail(authenticationResult.getEmail())).thenReturn(Optional.of(account));
        when(authenticationExecutiveService.successLogin(account)).thenReturn(loginResponse);
        doNothing().when(customerService).createCustomer(account, authenticationResult);

        //when
        final LoginResponse response = authenticationService.loginWithAuthenticationProvider(request);

        //then
        assertThat(response).isEqualTo(loginResponse);
    }

    @Test
    void givenNotExistingCustomerAccountAndCustomerWhenLoginWithAuthenticationProviderThenLoginAndCreateAccount() {
        //given
        final ExternalAuthenticationProviderLoginRequest request = ExternalAuthenticationDataGenerator.facebook(1);
        final ExternalAuthenticationResult authenticationResult = ExternalAuthenticationDataGenerator.externalAuthenticationResult(1);
        final Account account = AccountDataGenerator.customerAccount(1);
        account.setCustomer(Customer.builder().build());
        final LoginResponse loginResponse = LoginResponse.builder().build();

        when(externalAuthenticationDelegationService.authenticate(request)).thenReturn(authenticationResult);
        when(accountService.existsByMail(authenticationResult.getEmail())).thenReturn(false);
        when(accountService.createAccount(authenticationResult)).thenReturn(account);
        when(authenticationExecutiveService.successLogin(account)).thenReturn(loginResponse);

        //when
        final LoginResponse response = authenticationService.loginWithAuthenticationProvider(request);

        //then
        assertThat(response).isEqualTo(loginResponse);
    }

    @Test
    void givenExistingCustomerAccountNotCustomerWhenLoginWithAuthenticationProviderThenError() {
        //given
        final ExternalAuthenticationProviderLoginRequest request = ExternalAuthenticationDataGenerator.facebook(1);
        final ExternalAuthenticationResult authenticationResult = ExternalAuthenticationDataGenerator.externalAuthenticationResult(1);
        final Account account = AccountDataGenerator.customerAccount(1);
        account.setCustomer(Customer.builder().build());
        account.setAccountType(AccountType.COMPANY);

        when(externalAuthenticationDelegationService.authenticate(request)).thenReturn(authenticationResult);
        when(accountService.existsByMail(authenticationResult.getEmail())).thenReturn(true);
        when(accountService.findActiveAccountByMail(authenticationResult.getEmail())).thenReturn(Optional.of(account));

        //when
        final Throwable throwable = catchThrowable(() -> authenticationService.loginWithAuthenticationProvider(request));

        //then
        assertThat(throwable).isInstanceOf(UnauthorizedRestException.class);
        assertThat(((UnauthorizedRestException) (throwable)).getCode()).isEqualTo(RestExceptionStatusCode.LOGIN_PROVIDER_CUSTOMER_ACCOUNT_NOT_ACTIVE.ordinal());
    }
}