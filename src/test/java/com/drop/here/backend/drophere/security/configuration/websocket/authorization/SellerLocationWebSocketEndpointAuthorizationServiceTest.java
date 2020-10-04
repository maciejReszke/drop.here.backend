package com.drop.here.backend.drophere.security.configuration.websocket.authorization;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.enums.AccountType;
import com.drop.here.backend.drophere.authentication.account.service.AccountProfileService;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.drop.service.DropService;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SellerLocationWebSocketEndpointAuthorizationServiceTest {
    @InjectMocks
    private SellerLocationWebSocketEndpointAuthorizationService sellerLocationWebSocketEndpointAuthorizationService;

    @Mock
    private DropService dropService;

    @Mock
    private AccountProfileService accountProfileService;

    @Test
    void givenCompanyAuthenticationSuccessfullyAuthorizedWhenAuthorizeThenTrue() {
        //given
        final Account account = Account.builder().accountType(AccountType.COMPANY).build();
        final AccountAuthentication accountAuthentication = AccountAuthentication.builder()
                .account(account)
                .build();
        final String profileUid = "profileUid123";

        when(accountProfileService.existsByAccountAndProfileUid(account, profileUid)).thenReturn(true);

        //when
        final boolean authorize = sellerLocationWebSocketEndpointAuthorizationService.authorize(accountAuthentication, profileUid);

        //then
        assertThat(authorize).isTrue();
    }

    @Test
    void givenCompanyAuthenticationFailingAuthorizedWhenAuthorizeThenFalse() {
        //given
        final Account account = Account.builder().accountType(AccountType.COMPANY).build();
        final AccountAuthentication accountAuthentication = AccountAuthentication.builder()
                .account(account)
                .build();
        final String profileUid = "profileUid123";

        when(accountProfileService.existsByAccountAndProfileUid(account, profileUid)).thenReturn(false);

        //when
        final boolean authorize = sellerLocationWebSocketEndpointAuthorizationService.authorize(accountAuthentication, profileUid);

        //then
        assertThat(authorize).isFalse();
    }

    @Test
    void givenCustomerAuthenticationSuccessfullyAuthorizedWhenAuthorizeThenTrue() {
        //given
        final Account account = Account.builder().accountType(AccountType.CUSTOMER).build();
        final Customer customer = Customer.builder().build();
        final AccountAuthentication accountAuthentication = AccountAuthentication.builder()
                .account(account)
                .customer(customer)
                .build();
        final String profileUid = "profileUid123";

        when(dropService.isSellerLocationAvailableForCustomer(profileUid, customer)).thenReturn(true);

        //when
        final boolean authorize = sellerLocationWebSocketEndpointAuthorizationService.authorize(accountAuthentication, profileUid);

        //then
        assertThat(authorize).isTrue();
    }

    @Test
    void givenCustomerAuthenticationFailingAuthorizedWhenAuthorizeThenFalse() {
        //given
        final Account account = Account.builder().accountType(AccountType.CUSTOMER).build();
        final Customer customer = Customer.builder().build();
        final AccountAuthentication accountAuthentication = AccountAuthentication.builder()
                .account(account)
                .customer(customer)
                .build();
        final String profileUid = "profileUid123";

        when(dropService.isSellerLocationAvailableForCustomer(profileUid, customer)).thenReturn(false);

        //when
        final boolean authorize = sellerLocationWebSocketEndpointAuthorizationService.authorize(accountAuthentication, profileUid);

        //then
        assertThat(authorize).isFalse();
    }
}