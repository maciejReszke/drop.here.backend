package com.drop.here.backend.drophere.notification.service;

import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.notification.dto.NotificationCreationRequest;
import com.drop.here.backend.drophere.notification.dto.NotificationManagementRequest;
import com.drop.here.backend.drophere.notification.dto.NotificationResponse;
import com.drop.here.backend.drophere.notification.entity.Notification;
import com.drop.here.backend.drophere.notification.entity.NotificationJob;
import com.drop.here.backend.drophere.notification.entity.NotificationToken;
import com.drop.here.backend.drophere.notification.enums.NotificationBroadcastingType;
import com.drop.here.backend.drophere.notification.enums.NotificationCategory;
import com.drop.here.backend.drophere.notification.enums.NotificationReadStatus;
import com.drop.here.backend.drophere.notification.enums.NotificationRecipientType;
import com.drop.here.backend.drophere.notification.enums.NotificationReferencedSubjectType;
import com.drop.here.backend.drophere.notification.enums.NotificationType;
import com.drop.here.backend.drophere.test_data.NotificationDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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

    @Test
    void givenToCustomerNotificationWhenToNotificationsThenMap() {
        //given
        final Customer customer1 = Customer.builder().id(1L).build();
        final Customer customer2 = Customer.builder().id(2L).build();
        final Customer customer3 = Customer.builder().id(3L).build();
        final NotificationCreationRequest notificationCreationRequest = NotificationCreationRequest.builder()
                .notificationCategory(NotificationCategory.DROP_STATUS_CHANGE)
                .notificationType(NotificationType.NOTIFICATION_PANEL)
                .broadcastingCompany(null)
                .broadcastingCustomer(customer1)
                .broadcastingType(NotificationBroadcastingType.CUSTOMER)
                .detailedMessage("detailedMessage")
                .message("message")
                .recipientAccountProfiles(List.of())
                .recipientCompanies(List.of())
                .recipientCustomers(List.of(customer2, customer3))
                .referencedSubjectId("referencedSubjectId")
                .referencedSubjectType(NotificationReferencedSubjectType.EMPTY)
                .title("title123")
                .build();

        //when
        final List<Notification> results = notificationMappingService.toNotifications(notificationCreationRequest);

        //then
        assertThat(results).hasSize(2);
        results.forEach(result -> {
            assertThat(result.getReadStatus()).isEqualTo(NotificationReadStatus.UNREAD);
            assertThat(result.getCreatedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now());
            assertThat(result.getDetailedMessage()).isEqualTo(notificationCreationRequest.getDetailedMessage());
            assertThat(result.getMessage()).isEqualTo(notificationCreationRequest.getMessage());
            assertThat(result.getReferencedSubjectId()).isEqualTo(notificationCreationRequest.getReferencedSubjectId());
            assertThat(result.getTitle()).isEqualTo(notificationCreationRequest.getTitle());
            assertThat(result.getBroadcastingCompany()).isNull();
            assertThat(result.getBroadcastingCustomer()).isEqualTo(customer1);
            assertThat(result.getBroadcastingType()).isEqualTo(notificationCreationRequest.getBroadcastingType());
            assertThat(result.getCategory()).isEqualTo(notificationCreationRequest.getNotificationCategory());
            assertThat(result.getRecipientAccountProfile()).isNull();
            assertThat(result.getRecipientCompany()).isNull();
            assertThat(result.getRecipientType()).isEqualTo(NotificationRecipientType.CUSTOMER);
            assertThat(result.getType()).isEqualTo(notificationCreationRequest.getNotificationType());
        });
        assertThat(results.get(0).getRecipientCustomer()).isEqualTo(customer2);
        assertThat(results.get(1).getRecipientCustomer()).isEqualTo(customer3);
    }

    @Test
    void givenToCompanyNotificationWhenToNotificationsThenMap() {
        //given
        final Company company1 = Company.builder().id(1L).build();
        final Company company2 = Company.builder().id(1L).build();
        final Company company3 = Company.builder().id(1L).build();
        final NotificationCreationRequest notificationCreationRequest = NotificationCreationRequest.builder()
                .notificationCategory(NotificationCategory.DROP_STATUS_CHANGE)
                .notificationType(NotificationType.NOTIFICATION_PANEL)
                .broadcastingCompany(company1)
                .broadcastingCustomer(null)
                .broadcastingType(NotificationBroadcastingType.COMPANY)
                .detailedMessage("detailedMessage")
                .message("message")
                .recipientAccountProfiles(List.of())
                .recipientCompanies(List.of(company2, company3))
                .recipientCustomers(List.of())
                .referencedSubjectId("referencedSubjectId")
                .referencedSubjectType(NotificationReferencedSubjectType.EMPTY)
                .title("title123")
                .build();

        //when
        final List<Notification> results = notificationMappingService.toNotifications(notificationCreationRequest);

        //then
        assertThat(results).hasSize(2);
        results.forEach(result -> {
            assertThat(result.getReadStatus()).isEqualTo(NotificationReadStatus.UNREAD);
            assertThat(result.getCreatedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now());
            assertThat(result.getDetailedMessage()).isEqualTo(notificationCreationRequest.getDetailedMessage());
            assertThat(result.getMessage()).isEqualTo(notificationCreationRequest.getMessage());
            assertThat(result.getReferencedSubjectId()).isEqualTo(notificationCreationRequest.getReferencedSubjectId());
            assertThat(result.getTitle()).isEqualTo(notificationCreationRequest.getTitle());
            assertThat(result.getBroadcastingCompany()).isEqualTo(company1);
            assertThat(result.getBroadcastingCustomer()).isNull();
            assertThat(result.getBroadcastingType()).isEqualTo(notificationCreationRequest.getBroadcastingType());
            assertThat(result.getCategory()).isEqualTo(notificationCreationRequest.getNotificationCategory());
            assertThat(result.getRecipientAccountProfile()).isNull();
            assertThat(result.getRecipientCustomer()).isNull();
            assertThat(result.getRecipientType()).isEqualTo(NotificationRecipientType.COMPANY);
            assertThat(result.getType()).isEqualTo(notificationCreationRequest.getNotificationType());
        });
        assertThat(results.get(0).getRecipientCompany()).isEqualTo(company2);
        assertThat(results.get(1).getRecipientCompany()).isEqualTo(company3);
    }

    @Test
    void givenToCompanyProfileNotificationWhenToNotificationsThenMap() {
        //given
        final Company company1 = Company.builder().id(1L).build();
        final AccountProfile accountProfile1 = AccountProfile.builder().id(1L).build();
        final AccountProfile accountProfile2 = AccountProfile.builder().id(2L).build();
        final NotificationCreationRequest notificationCreationRequest = NotificationCreationRequest.builder()
                .notificationCategory(NotificationCategory.DROP_STATUS_CHANGE)
                .notificationType(NotificationType.NOTIFICATION_PANEL)
                .broadcastingCompany(company1)
                .broadcastingCustomer(null)
                .broadcastingType(NotificationBroadcastingType.COMPANY)
                .detailedMessage("detailedMessage")
                .message("message")
                .recipientAccountProfiles(List.of(accountProfile1, accountProfile2))
                .recipientCompanies(List.of())
                .recipientCustomers(List.of())
                .referencedSubjectId("referencedSubjectId")
                .referencedSubjectType(NotificationReferencedSubjectType.EMPTY)
                .title("title123")
                .build();

        //when
        final List<Notification> results = notificationMappingService.toNotifications(notificationCreationRequest);

        //then
        assertThat(results).hasSize(2);
        results.forEach(result -> {
            assertThat(result.getReadStatus()).isEqualTo(NotificationReadStatus.UNREAD);
            assertThat(result.getCreatedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now());
            assertThat(result.getDetailedMessage()).isEqualTo(notificationCreationRequest.getDetailedMessage());
            assertThat(result.getMessage()).isEqualTo(notificationCreationRequest.getMessage());
            assertThat(result.getReferencedSubjectId()).isEqualTo(notificationCreationRequest.getReferencedSubjectId());
            assertThat(result.getTitle()).isEqualTo(notificationCreationRequest.getTitle());
            assertThat(result.getBroadcastingCompany()).isEqualTo(company1);
            assertThat(result.getBroadcastingCustomer()).isNull();
            assertThat(result.getBroadcastingType()).isEqualTo(notificationCreationRequest.getBroadcastingType());
            assertThat(result.getCategory()).isEqualTo(notificationCreationRequest.getNotificationCategory());
            assertThat(result.getRecipientCustomer()).isNull();
            assertThat(result.getRecipientCompany()).isNull();
            assertThat(result.getRecipientType()).isEqualTo(NotificationRecipientType.COMPANY_PROFILE);
            assertThat(result.getType()).isEqualTo(notificationCreationRequest.getNotificationType());
        });
        assertThat(results.get(0).getRecipientAccountProfile()).isEqualTo(accountProfile1);
        assertThat(results.get(1).getRecipientAccountProfile()).isEqualTo(accountProfile2);
    }

}