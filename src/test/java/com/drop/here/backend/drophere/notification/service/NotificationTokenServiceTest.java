package com.drop.here.backend.drophere.notification.service;

import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.common.rest.ResourceOperationStatus;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.notification.dto.NotificationTokenManagementRequest;
import com.drop.here.backend.drophere.notification.entity.NotificationToken;
import com.drop.here.backend.drophere.notification.enums.NotificationBroadcastingServiceType;
import com.drop.here.backend.drophere.notification.enums.NotificationTokenType;
import com.drop.here.backend.drophere.notification.repository.NotificationTokenRepository;
import com.drop.here.backend.drophere.notification.service.token.NotificationTokenMappingService;
import com.drop.here.backend.drophere.notification.service.token.NotificationTokenService;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationTokenServiceTest {
    @InjectMocks
    private NotificationTokenService notificationTokenService;

    @Mock
    private NotificationTokenRepository notificationTokenRepository;

    @Mock
    private NotificationTokenMappingService notificationTokenMappingService;

    @Test
    void givenExistingTokenWhenUpdateNotificationTokenThenUpdate() {
        //given
        final NotificationToken mappedNotificationToken = NotificationToken.builder().build();
        final Customer customer = Customer.builder().build();
        final NotificationToken savedNotificationToken = NotificationToken.builder()
                .tokenType(NotificationTokenType.CUSTOMER)
                .broadcastingServiceType(NotificationBroadcastingServiceType.FIREBASE)
                .ownerCustomer(customer)
                .build();

        final AccountAuthentication accountAuthentication = AccountAuthentication.builder().build();
        final NotificationTokenManagementRequest notificationTokenManagementRequest = NotificationTokenManagementRequest.builder().build();

        when(notificationTokenMappingService.toNotificationToken(accountAuthentication, notificationTokenManagementRequest))
                .thenReturn(mappedNotificationToken);
        when(notificationTokenRepository.findByOwnerCustomerAndBroadcastingServiceType(customer, NotificationBroadcastingServiceType.FIREBASE))
                .thenReturn(Optional.of(savedNotificationToken));
        when(notificationTokenRepository.save(savedNotificationToken)).thenReturn(savedNotificationToken);

        //when
        final ResourceOperationResponse result = notificationTokenService.updateNotificationToken(accountAuthentication, notificationTokenManagementRequest);

        //then
        assertThat(result.getOperationStatus()).isEqualTo(ResourceOperationStatus.UPDATED);
    }

    @Test
    void givenNotExistingTokenWhenUpdateNotificationTokenThenSave() {
        //given
        final Customer customer = Customer.builder().build();
        final NotificationToken mappedNotificationToken = NotificationToken.builder()
                .tokenType(NotificationTokenType.CUSTOMER)
                .broadcastingServiceType(NotificationBroadcastingServiceType.FIREBASE)
                .ownerCustomer(customer)
                .build();

        final AccountAuthentication accountAuthentication = AccountAuthentication.builder().build();
        final NotificationTokenManagementRequest notificationTokenManagementRequest = NotificationTokenManagementRequest.builder().build();

        when(notificationTokenMappingService.toNotificationToken(accountAuthentication, notificationTokenManagementRequest))
                .thenReturn(mappedNotificationToken);
        when(notificationTokenRepository.findByOwnerCustomerAndBroadcastingServiceType(customer, NotificationBroadcastingServiceType.FIREBASE))
                .thenReturn(Optional.empty());
        when(notificationTokenRepository.save(mappedNotificationToken)).thenReturn(mappedNotificationToken);

        //when
        final ResourceOperationResponse result = notificationTokenService.updateNotificationToken(accountAuthentication, notificationTokenManagementRequest);

        //then
        assertThat(result.getOperationStatus()).isEqualTo(ResourceOperationStatus.UPDATED);
    }
}