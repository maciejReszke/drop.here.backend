package com.drop.here.backend.drophere.notification.service.token;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.authentication.account.enums.AccountType;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.notification.dto.NotificationTokenManagementRequest;
import com.drop.here.backend.drophere.notification.entity.NotificationToken;
import com.drop.here.backend.drophere.notification.enums.NotificationBroadcastingServiceType;
import com.drop.here.backend.drophere.notification.enums.NotificationTokenType;
import com.drop.here.backend.drophere.configuration.security.AccountAuthentication;
import com.drop.here.backend.drophere.test_data.AuthenticationDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class NotificationTokenMappingServiceTest {

    @InjectMocks
    private NotificationTokenMappingService mappingService;

    @Test
    void givenCustomerAuthenticationWhenToNotificationTokenThenMap() {
        //given
        final Customer customer = Customer.builder().build();
        final Account account = Account.builder().customer(customer).accountType(AccountType.CUSTOMER).build();
        final AccountAuthentication authentication = AuthenticationDataGenerator.accountAuthentication(account);
        final NotificationTokenManagementRequest request = NotificationTokenManagementRequest.builder()
                .token("token")
                .broadcastingServiceType(NotificationBroadcastingServiceType.FIREBASE.name())
                .build();

        //when
        final NotificationToken result = mappingService.toNotificationToken(authentication, request);

        //then
        assertThat(result.getBroadcastingServiceType()).isEqualTo(NotificationBroadcastingServiceType.FIREBASE);
        assertThat(result.getOwnerAccountProfile()).isNull();
        assertThat(result.getOwnerCustomer()).isEqualTo(customer);
        assertThat(result.getToken()).isEqualTo("token");
        assertThat(result.getTokenType()).isEqualTo(NotificationTokenType.CUSTOMER);
    }

    @Test
    void givenCompanyAuthenticationWhenToNotificationTokenThenMap() {
        //given
        final Account account = Account.builder().accountType(AccountType.COMPANY).build();
        final AccountProfile profile = AccountProfile.builder().build();
        final AccountAuthentication authentication = AuthenticationDataGenerator.accountAuthenticationWithProfile(account, profile);
        final NotificationTokenManagementRequest request = NotificationTokenManagementRequest.builder()
                .token("token")
                .broadcastingServiceType(NotificationBroadcastingServiceType.FIREBASE.name())
                .build();

        //when
        final NotificationToken result = mappingService.toNotificationToken(authentication, request);

        //then
        assertThat(result.getBroadcastingServiceType()).isEqualTo(NotificationBroadcastingServiceType.FIREBASE);
        assertThat(result.getOwnerAccountProfile()).isEqualTo(profile);
        assertThat(result.getOwnerCustomer()).isNull();
        assertThat(result.getToken()).isEqualTo("token");
        assertThat(result.getTokenType()).isEqualTo(NotificationTokenType.PROFILE);
    }

}