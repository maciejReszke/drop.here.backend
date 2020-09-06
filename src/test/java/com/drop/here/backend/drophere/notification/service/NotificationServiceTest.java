package com.drop.here.backend.drophere.notification.service;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.common.rest.ResourceOperationStatus;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.notification.dto.NotificationManagementRequest;
import com.drop.here.backend.drophere.notification.dto.NotificationResponse;
import com.drop.here.backend.drophere.notification.entity.Notification;
import com.drop.here.backend.drophere.notification.enums.NotificationReadStatus;
import com.drop.here.backend.drophere.notification.repository.NotificationRepository;
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
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @InjectMocks
    private NotificationService notificationService;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationMappingService notificationMappingService;

    @Mock
    private NotificationValidationService notificationValidationService;

    @Test
    void givenReadStatusCompanyPrincipalWhenFindNotificationThenFind() {
        //given
        final Company company = Company.builder().build();
        final Account account = AccountDataGenerator.companyAccount(1)
                .toBuilder()
                .company(company)
                .build();
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);
        final String readStatus = NotificationReadStatus.READ.name();
        final Pageable pageable = Pageable.unpaged();
        final NotificationResponse response = NotificationResponse.builder().build();
        final Notification notification = NotificationDataGenerator.companyNotification(1, company);

        when(notificationRepository.findByRecipientCompanyAndReadStatusIn(company, List.of(NotificationReadStatus.READ), pageable))
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

        when(notificationRepository.findByRecipientCustomerAndReadStatusIn(customer, Arrays.asList(NotificationReadStatus.values()), pageable))
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

        when(notificationRepository.findByIdAndRecipientCustomer(notificationId, customer))
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
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);
        final Long notificationId = 5L;
        final NotificationManagementRequest notificationManagementRequest = NotificationManagementRequest.builder().build();

        when(notificationRepository.findByIdAndRecipientCompany(notificationId, company))
                .thenReturn(Optional.empty());

        //when
        final Throwable throwable = catchThrowable(() -> notificationService.updateNotification(accountAuthentication, notificationId, notificationManagementRequest));

        //then
        assertThat(throwable).isInstanceOf(RestEntityNotFoundException.class);
    }

}