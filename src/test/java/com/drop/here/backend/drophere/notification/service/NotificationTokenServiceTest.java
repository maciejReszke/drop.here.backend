package com.drop.here.backend.drophere.notification.service;

import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.common.rest.ResourceOperationStatus;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.notification.dto.NotificationTokenManagementRequest;
import com.drop.here.backend.drophere.notification.entity.Notification;
import com.drop.here.backend.drophere.notification.entity.NotificationToken;
import com.drop.here.backend.drophere.notification.enums.NotificationBroadcastingServiceType;
import com.drop.here.backend.drophere.notification.enums.NotificationRecipientType;
import com.drop.here.backend.drophere.notification.enums.NotificationTokenType;
import com.drop.here.backend.drophere.notification.repository.NotificationTokenRepository;
import com.drop.here.backend.drophere.notification.service.token.NotificationAndTokenWrapper;
import com.drop.here.backend.drophere.notification.service.token.NotificationTokenMappingService;
import com.drop.here.backend.drophere.notification.service.token.NotificationTokenService;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
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
        final Customer customer = Customer.builder().build();
        final NotificationToken mappedNotificationToken = NotificationToken.builder()
                .tokenType(NotificationTokenType.CUSTOMER)
                .ownerCustomer(customer)
                .broadcastingServiceType(NotificationBroadcastingServiceType.FIREBASE)
                .build();
        final NotificationToken savedNotificationToken = NotificationToken.builder()
                .ownerCustomer(customer)
                .tokenType(NotificationTokenType.CUSTOMER)
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

    @Test
    void givenToCompanyProfileNotificationWhenJoinTokensThenJoin() {
        //given
        final AccountProfile profile1 = AccountProfile.builder().id(1L).build();
        final AccountProfile profile2 = AccountProfile.builder().id(2L).build();
        final Notification notification1 = Notification.builder()
                .id(1L)
                .recipientType(NotificationRecipientType.COMPANY_PROFILE)
                .recipientAccountProfile(profile1)
                .build();

        final Notification notification2 = Notification.builder()
                .id(2L)
                .recipientType(NotificationRecipientType.COMPANY_PROFILE)
                .recipientAccountProfile(profile2)
                .build();

        final NotificationToken token = NotificationToken.builder().ownerAccountProfile(profile2).build();
        when(notificationTokenRepository.findByOwnerAccountProfileInAndBroadcastingServiceType(List.of(profile1, profile2), NotificationBroadcastingServiceType.FIREBASE))
                .thenReturn(List.of(token));

        //when
        final List<NotificationAndTokenWrapper> result = notificationTokenService.joinTokens(List.of(notification1, notification2), NotificationBroadcastingServiceType.FIREBASE);

        //then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getNotification()).isEqualTo(notification1);
        assertThat(result.get(0).getToken()).isEmpty();
        assertThat(result.get(1).getNotification()).isEqualTo(notification2);
        assertThat(result.get(1).getToken().orElseThrow()).isEqualTo(token);
    }

    @Test
    void givenToCompanyNotificationWhenJoinTokensThenJoin() {
        //given
        final Notification notification1 = Notification.builder()
                .id(1L)
                .recipientType(NotificationRecipientType.COMPANY)
                .build();

        final Notification notification2 = Notification.builder()
                .id(2L)
                .recipientType(NotificationRecipientType.COMPANY)
                .build();

        //when
        final List<NotificationAndTokenWrapper> result = notificationTokenService.joinTokens(List.of(notification1, notification2), NotificationBroadcastingServiceType.FIREBASE);

        //then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getNotification()).isEqualTo(notification1);
        assertThat(result.get(0).getToken()).isEmpty();
        assertThat(result.get(1).getNotification()).isEqualTo(notification2);
        assertThat(result.get(1).getToken()).isEmpty();
    }

    @Test
    void givenToCustomerNotificationWhenJoinTokensThenJoin() {
        //given
        final Customer customer1 = Customer.builder().id(1L).build();
        final Customer customer2 = Customer.builder().id(2L).build();
        final Notification notification1 = Notification.builder()
                .id(1L)
                .recipientType(NotificationRecipientType.CUSTOMER)
                .recipientCustomer(customer1)
                .build();

        final Notification notification2 = Notification.builder()
                .id(2L)
                .recipientType(NotificationRecipientType.CUSTOMER)
                .recipientCustomer(customer2)
                .build();

        final NotificationToken token = NotificationToken.builder().ownerCustomer(customer2).build();
        when(notificationTokenRepository.findByOwnerCustomerInAndBroadcastingServiceType(List.of(customer1, customer2), NotificationBroadcastingServiceType.FIREBASE))
                .thenReturn(List.of(token));

        //when
        final List<NotificationAndTokenWrapper> result = notificationTokenService.joinTokens(List.of(notification1, notification2), NotificationBroadcastingServiceType.FIREBASE);

        //then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getNotification()).isEqualTo(notification1);
        assertThat(result.get(0).getToken()).isEmpty();
        assertThat(result.get(1).getNotification()).isEqualTo(notification2);
        assertThat(result.get(1).getToken().orElseThrow()).isEqualTo(token);
    }
}