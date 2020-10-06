package com.drop.here.backend.drophere.notification.service;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.common.rest.ResourceOperationStatus;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.notification.dto.NotificationCreationRequest;
import com.drop.here.backend.drophere.notification.dto.NotificationManagementRequest;
import com.drop.here.backend.drophere.notification.dto.NotificationResponse;
import com.drop.here.backend.drophere.notification.entity.Notification;
import com.drop.here.backend.drophere.notification.entity.NotificationJob;
import com.drop.here.backend.drophere.notification.entity.NotificationToken;
import com.drop.here.backend.drophere.notification.enums.NotificationBroadcastingServiceType;
import com.drop.here.backend.drophere.notification.enums.NotificationReadStatus;
import com.drop.here.backend.drophere.notification.enums.NotificationType;
import com.drop.here.backend.drophere.notification.repository.NotificationJobRepository;
import com.drop.here.backend.drophere.notification.repository.NotificationRepository;
import com.drop.here.backend.drophere.notification.service.broadcasting.NotificationBroadcastingService;
import com.drop.here.backend.drophere.notification.service.broadcasting.NotificationBroadcastingServiceFactory;
import com.drop.here.backend.drophere.notification.service.token.NotificationAndTokenWrapper;
import com.drop.here.backend.drophere.notification.service.token.NotificationTokenService;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import com.drop.here.backend.drophere.test_data.AuthenticationDataGenerator;
import com.drop.here.backend.drophere.test_data.NotificationDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @InjectMocks
    private NotificationService notificationService;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationJobRepository notificationJobRepository;

    @Mock
    private NotificationMappingService notificationMappingService;

    @Mock
    private NotificationValidationService notificationValidationService;

    @Mock
    private NotificationBroadcastingServiceFactory notificationBroadcastingServiceFactory;

    @Mock
    private NotificationBroadcastingService notificationBroadcastingService;

    @Mock
    private NotificationTokenService notificationTokenService;

    @Test
    void givenReadStatusCompanyPrincipalWhenFindNotificationThenFind() {
        //given
        final Company company = Company.builder().build();
        final Account account = AccountDataGenerator.companyAccount(1)
                .toBuilder()
                .company(company)
                .build();
        final AccountProfile accountProfile = AccountProfile.builder().build();
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthenticationWithProfile(account, accountProfile);
        final String readStatus = NotificationReadStatus.READ.name();
        final Pageable pageable = Pageable.unpaged();
        final NotificationResponse response = NotificationResponse.builder().build();
        final Notification notification = NotificationDataGenerator.companyNotification(1, company);

        when(notificationRepository.findByRecipientCompanyOrRecipientAccountProfileAndReadStatusIn(company, accountProfile, List.of(NotificationReadStatus.READ), pageable))
                .thenReturn(new PageImpl<>(List.of(notification)));
        when(notificationMappingService.toNotificationResponse(notification)).thenReturn(response);

        //when
        final Page<NotificationResponse> notifications = notificationService.findNotifications(accountAuthentication, readStatus, pageable);

        //then
        assertThat(notifications.get()).hasSize(1);
        assertThat(notifications.stream().findFirst().orElseThrow()).isEqualTo(response);
    }

    @Test
    void givenNullReadStatusCustomerPrincipalWhenFindNotificationThenFind() {
        //given
        final Customer customer = Customer.builder().build();
        final Account account = AccountDataGenerator.customerAccount(1)
                .toBuilder()
                .customer(customer)
                .build();
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);
        final String readStatus = null;
        final Pageable pageable = Pageable.unpaged();
        final NotificationResponse response = NotificationResponse.builder().build();
        final Notification notification = NotificationDataGenerator.customerNotification(1, customer);

        when(notificationRepository.findByRecipientCustomerAndReadStatusInAndType(
                customer, Arrays.asList(NotificationReadStatus.values()), NotificationType.NOTIFICATION_PANEL, pageable))
                .thenReturn(new PageImpl<>(List.of(notification)));
        when(notificationMappingService.toNotificationResponse(notification)).thenReturn(response);

        //when
        final Page<NotificationResponse> notifications = notificationService.findNotifications(accountAuthentication, readStatus, pageable);

        //then
        assertThat(notifications.get()).hasSize(1);
        assertThat(notifications.stream().findFirst().orElseThrow()).isEqualTo(response);
    }

    @Test
    void givenExistingCustomerNotificationWhenUpdateThenUpdate() {
        //given
        final Customer customer = Customer.builder().build();
        final Account account = AccountDataGenerator.customerAccount(1)
                .toBuilder()
                .customer(customer)
                .build();
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);
        final Long notificationId = 5L;
        final NotificationManagementRequest notificationManagementRequest = NotificationManagementRequest.builder().build();

        final Notification notification = NotificationDataGenerator.customerNotification(1, customer);

        when(notificationRepository.findByIdAndRecipientCustomerAndType(notificationId, customer, NotificationType.NOTIFICATION_PANEL))
                .thenReturn(Optional.of(notification));
        doNothing().when(notificationValidationService).validateUpdateNotificationRequest(notificationManagementRequest);
        doNothing().when(notificationMappingService).update(notification, notificationManagementRequest);

        //when
        final ResourceOperationResponse result = notificationService.updateNotification(accountAuthentication, notificationId, notificationManagementRequest);

        //then
        assertThat(result.getOperationStatus()).isEqualTo(ResourceOperationStatus.UPDATED);
    }

    @Test
    void givenNotExistingCompanyNotificationWhenUpdateThenThrowException() {
        //given
        final Company company = Company.builder().build();
        final Account account = AccountDataGenerator.companyAccount(1)
                .toBuilder()
                .company(company)
                .build();
        final AccountProfile accountProfile = AccountProfile.builder().build();
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthenticationWithProfile(account, accountProfile);
        final Long notificationId = 5L;
        final NotificationManagementRequest notificationManagementRequest = NotificationManagementRequest.builder().build();

        when(notificationRepository.findByIdAndRecipientCompanyOrRecipientAccountProfileAndType(
                notificationId, company, accountProfile, NotificationType.NOTIFICATION_PANEL))
                .thenReturn(Optional.empty());

        //when
        final Throwable throwable = catchThrowable(() -> notificationService.updateNotification(accountAuthentication, notificationId, notificationManagementRequest));

        //then
        assertThat(throwable).isInstanceOf(RestEntityNotFoundException.class);
    }

    @Test
    void givenNotificationsSuccessSendWhenSendNotificationsThenSendAndUpdateBroadcastingStatus() {
        //given
        final List<NotificationJob> notificationJobs = List.of(NotificationJob.builder()
                .notification(Notification.builder().build()).build());

        when(notificationBroadcastingServiceFactory.getNotificationBroadcastingService()).thenReturn(notificationBroadcastingService);
        when(notificationBroadcastingService.getBatchAmount()).thenReturn(50);
        when(notificationJobRepository.findAllByNotificationIsNotNull(PageRequest.of(0, 50))).thenReturn(notificationJobs);
        when(notificationBroadcastingService.sendBatch(notificationJobs)).thenReturn(true);
        doNothing().when(notificationJobRepository).deleteByNotificationJobIn(notificationJobs);
        doNothing().when(notificationRepository).deletePushOnlyNotifications(any());
        //when
        notificationService.sendNotifications();

        //then
        verifyNoMoreInteractions(notificationRepository);
    }

    @Test
    void givenNotificationsFailureSendWhenSendNotificationsThenDoNothing() {
        //given
        final List<NotificationJob> notificationJobs = List.of(NotificationJob.builder().build());

        when(notificationBroadcastingServiceFactory.getNotificationBroadcastingService()).thenReturn(notificationBroadcastingService);
        when(notificationBroadcastingService.getBatchAmount()).thenReturn(50);
        when(notificationJobRepository.findAllByNotificationIsNotNull(PageRequest.of(0, 50))).thenReturn(notificationJobs);
        when(notificationBroadcastingService.sendBatch(notificationJobs)).thenReturn(false);

        //when
        notificationService.sendNotifications();

        //then
        verifyNoMoreInteractions(notificationJobRepository);
    }

    @Test
    void givenNotificationRequestTypeNotificationPanelWithoutTokenWhenCreateNotificationsThenCreate() {
        //given
        final NotificationCreationRequest notificationCreationRequest = NotificationCreationRequest.builder().build();
        final Notification notification = Notification.builder().type(NotificationType.NOTIFICATION_PANEL)
                .build();
        final NotificationAndTokenWrapper notificationAndTokenWrapper =
                new NotificationAndTokenWrapper(notification, Optional.empty());

        when(notificationMappingService.toNotifications(notificationCreationRequest))
                .thenReturn(List.of(notification));
        when(notificationTokenService.joinTokens(any(), eq(NotificationBroadcastingServiceType.FIREBASE)))
                .thenReturn(List.of(notificationAndTokenWrapper));
        when(notificationRepository.saveAll(List.of(notification))).thenReturn(List.of(notification));
        when(notificationJobRepository.saveAll(List.of())).thenReturn(List.of());

        //when
        notificationService.createNotifications(notificationCreationRequest);

        //then
        verifyNoMoreInteractions(notificationMappingService);
    }

    @Test
    void givenNotificationRequestTypeNotificationPanelWithTokenWhenCreateNotificationsThenCreate() {
        //given
        final NotificationCreationRequest notificationCreationRequest = NotificationCreationRequest.builder().build();
        final Notification notification = Notification.builder().type(NotificationType.NOTIFICATION_PANEL)
                .build();
        final NotificationToken token = NotificationToken.builder().build();
        final NotificationAndTokenWrapper notificationAndTokenWrapper =
                new NotificationAndTokenWrapper(notification, Optional.of(token));
        final NotificationJob job = NotificationJob.builder().build();

        when(notificationMappingService.toNotifications(notificationCreationRequest))
                .thenReturn(List.of(notification));
        when(notificationTokenService.joinTokens(any(), eq(NotificationBroadcastingServiceType.FIREBASE)))
                .thenReturn(List.of(notificationAndTokenWrapper));
        when(notificationRepository.saveAll(List.of(notification))).thenReturn(List.of(notification));
        when(notificationJobRepository.saveAll(List.of(job))).thenReturn(List.of(job));
        when(notificationMappingService.toNotificationJob(notification, token)).thenReturn(job);

        //when
        notificationService.createNotifications(notificationCreationRequest);

        //then
        verifyNoMoreInteractions(notificationMappingService);
    }

    @Test
    void givenNotificationRequestTypeNotificationPushOnlyWithoutTokenWhenCreateNotificationsThenCreate() {
        //given
        final NotificationCreationRequest notificationCreationRequest = NotificationCreationRequest.builder().build();
        final Notification notification = Notification.builder().type(NotificationType.PUSH_NOTIFICATION_ONLY)
                .build();
        final NotificationAndTokenWrapper notificationAndTokenWrapper =
                new NotificationAndTokenWrapper(notification, Optional.empty());

        when(notificationMappingService.toNotifications(notificationCreationRequest))
                .thenReturn(List.of(notification));
        when(notificationTokenService.joinTokens(any(), eq(NotificationBroadcastingServiceType.FIREBASE)))
                .thenReturn(List.of(notificationAndTokenWrapper));
        when(notificationRepository.saveAll(List.of())).thenReturn(List.of());
        when(notificationJobRepository.saveAll(List.of())).thenReturn(List.of());

        //when
        notificationService.createNotifications(notificationCreationRequest);

        //then
        verifyNoMoreInteractions(notificationMappingService);
    }

    @Test
    void givenNotificationRequestTypeNotificationPushOnlyWithTokenWhenCreateNotificationsThenCreate() {
        //given
        final NotificationCreationRequest notificationCreationRequest = NotificationCreationRequest.builder().build();
        final Notification notification = Notification.builder().type(NotificationType.PUSH_NOTIFICATION_ONLY)
                .build();
        final NotificationToken token = NotificationToken.builder().build();
        final NotificationAndTokenWrapper notificationAndTokenWrapper =
                new NotificationAndTokenWrapper(notification, Optional.of(token));
        final NotificationJob job = NotificationJob.builder().build();

        when(notificationMappingService.toNotifications(notificationCreationRequest))
                .thenReturn(List.of(notification));
        when(notificationTokenService.joinTokens(any(), eq(NotificationBroadcastingServiceType.FIREBASE)))
                .thenReturn(List.of(notificationAndTokenWrapper));
        when(notificationRepository.saveAll(List.of(notification))).thenReturn(List.of(notification));
        when(notificationJobRepository.saveAll(List.of(job))).thenReturn(List.of(job));
        when(notificationMappingService.toNotificationJob(notification, token)).thenReturn(job);

        //when
        notificationService.createNotifications(notificationCreationRequest);

        //then
        verifyNoMoreInteractions(notificationMappingService);
    }
}