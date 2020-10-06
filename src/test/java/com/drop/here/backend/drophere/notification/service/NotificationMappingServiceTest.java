package com.drop.here.backend.drophere.notification.service;

import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.notification.dto.NotificationManagementRequest;
import com.drop.here.backend.drophere.notification.dto.NotificationResponse;
import com.drop.here.backend.drophere.notification.entity.Notification;
import com.drop.here.backend.drophere.notification.entity.NotificationJob;
import com.drop.here.backend.drophere.notification.entity.NotificationToken;
import com.drop.here.backend.drophere.notification.enums.NotificationBroadcastingType;
import com.drop.here.backend.drophere.notification.enums.NotificationReadStatus;
import com.drop.here.backend.drophere.test_data.NotificationDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class NotificationMappingServiceTest {

    @InjectMocks
    private NotificationMappingService notificationMappingService;

    @Test
    void givenNotificationAndRequestWhenUpdateThenUpdate() {
        //given
        final Notification notification = Notification.builder().build();
        final NotificationManagementRequest notificationManagementRequest = NotificationManagementRequest.builder()
                .readStatus(NotificationReadStatus.READ.name())
                .build();

        //when
        notificationMappingService.update(notification, notificationManagementRequest);

        //then
        assertThat(notification.getReadStatus()).isEqualTo(NotificationReadStatus.READ);
    }

    @Test
    void givenNotificationBroadcasterByCustomerWhenToNotificationResponseThenMap() {
        //given
        final Notification notification = NotificationDataGenerator.customerNotification(1, null);
        notification.setId(1L);
        notification.setBroadcastingType(NotificationBroadcastingType.CUSTOMER);
        final Customer customer = Customer.builder().id(5L).build();
        notification.setBroadcastingCustomer(customer);

        //when
        final NotificationResponse response = notificationMappingService.toNotificationResponse(notification);

        //then
        assertThat(response.getId()).isEqualTo(notification.getId());
        assertThat(response.getTitle()).isEqualTo(notification.getTitle());
        assertThat(response.getMessage()).isEqualTo(notification.getMessage());
        assertThat(response.getCategory()).isEqualTo(notification.getCategory());
        assertThat(response.getReadStatus()).isEqualTo(notification.getReadStatus());
        assertThat(response.getReferencedSubjectType()).isEqualTo(notification.getReferencedSubjectType());
        assertThat(response.getReferencedSubjectId()).isEqualTo("SYSTEM");
        assertThat(response.getBroadcastingType()).isEqualTo(notification.getBroadcastingType());
        assertThat(response.getBroadcasterId()).isEqualTo(customer.getId().toString());
        assertThat(response.getDetailedMessage()).isEqualTo(notification.getDetailedMessage());
        assertThat(response.getCreatedAt()).isEqualTo(notification.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    @Test
    void givenNotificationBroadcasterByCompanyWhenToNotificationResponseThenMap() {
        //given
        final Notification notification = NotificationDataGenerator.customerNotification(1, null);
        notification.setId(1L);
        notification.setBroadcastingType(NotificationBroadcastingType.COMPANY);
        final Company company = Company.builder().id(5L).build();
        notification.setBroadcastingCompany(company);

        //when
        final NotificationResponse response = notificationMappingService.toNotificationResponse(notification);

        //then
        assertThat(response.getId()).isEqualTo(notification.getId());
        assertThat(response.getTitle()).isEqualTo(notification.getTitle());
        assertThat(response.getMessage()).isEqualTo(notification.getMessage());
        assertThat(response.getCategory()).isEqualTo(notification.getCategory());
        assertThat(response.getReadStatus()).isEqualTo(notification.getReadStatus());
        assertThat(response.getReferencedSubjectType()).isEqualTo(notification.getReferencedSubjectType());
        assertThat(response.getReferencedSubjectId()).isEqualTo("SYSTEM");
        assertThat(response.getBroadcastingType()).isEqualTo(notification.getBroadcastingType());
        assertThat(response.getBroadcasterId()).isEqualTo(company.getId().toString());
        assertThat(response.getDetailedMessage()).isEqualTo(notification.getDetailedMessage());
        assertThat(response.getCreatedAt()).isEqualTo(notification.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    @Test
    void givenNotificationBroadcasterBySystemWhenToNotificationResponseThenMap() {
        //given
        final Notification notification = NotificationDataGenerator.customerNotification(1, null);
        notification.setId(1L);
        notification.setBroadcastingType(NotificationBroadcastingType.SYSTEM);

        //when
        final NotificationResponse response = notificationMappingService.toNotificationResponse(notification);

        //then
        assertThat(response.getId()).isEqualTo(notification.getId());
        assertThat(response.getTitle()).isEqualTo(notification.getTitle());
        assertThat(response.getMessage()).isEqualTo(notification.getMessage());
        assertThat(response.getCategory()).isEqualTo(notification.getCategory());
        assertThat(response.getReadStatus()).isEqualTo(notification.getReadStatus());
        assertThat(response.getReferencedSubjectType()).isEqualTo(notification.getReferencedSubjectType());
        assertThat(response.getReferencedSubjectId()).isEqualTo("SYSTEM");
        assertThat(response.getBroadcastingType()).isEqualTo(notification.getBroadcastingType());
        assertThat(response.getBroadcasterId()).isEqualTo("SYSTEM");
        assertThat(response.getDetailedMessage()).isEqualTo(notification.getDetailedMessage());
        assertThat(response.getCreatedAt()).isEqualTo(notification.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    @Test
    void givenNotificationAndTokenWhenToNotificationJobThenMap() {
        //given
        final Notification notification = Notification.builder().build();
        final NotificationToken notificationToken = NotificationToken.builder().build();

        //when
        final NotificationJob result = notificationMappingService.toNotificationJob(notification, notificationToken);

        //then
        assertThat(result.getCreatedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now());
        assertThat(result.getNotificationToken()).isEqualTo(notificationToken);
        assertThat(result.getNotification()).isEqualTo(notification);
    }

}