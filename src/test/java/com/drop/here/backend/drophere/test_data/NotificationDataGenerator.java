package com.drop.here.backend.drophere.test_data;

import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.notification.entity.Notification;
import com.drop.here.backend.drophere.notification.enums.NotificationBroadcastingType;
import com.drop.here.backend.drophere.notification.enums.NotificationCategory;
import com.drop.here.backend.drophere.notification.enums.NotificationReadStatus;
import com.drop.here.backend.drophere.notification.enums.NotificationRecipientType;
import com.drop.here.backend.drophere.notification.enums.NotificationReferencedSubjectType;
import com.drop.here.backend.drophere.notification.enums.NotificationType;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class NotificationDataGenerator {
    public Notification companyNotification(int i, Company company) {
        return baseNotification(i)
                .toBuilder()
                .recipientCompany(company)
                .recipientType(NotificationRecipientType.COMPANY)
                .build();
    }

    public Notification accountProfileNotification(int i, AccountProfile accountProfile) {
        return baseNotification(i)
                .toBuilder()
                .recipientAccountProfile(accountProfile)
                .recipientType(NotificationRecipientType.COMPANY_PROFILE)
                .build();
    }


    private Notification baseNotification(int i) {
        return Notification.builder()
                .createdAt(LocalDateTime.now())
                .message("Message" + i)
                .readStatus(NotificationReadStatus.UNREAD)
                .title("title" + i)
                .type(NotificationType.NOTIFICATION_PANEL)
                .category(NotificationCategory.TEST)
                .referencedSubjectType(NotificationReferencedSubjectType.EMPTY)
                .referencedSubjectId("id")
                .broadcastingType(NotificationBroadcastingType.SYSTEM)
                .build();
    }

    public Notification customerNotification(int i, Customer customer) {
        return baseNotification(i)
                .toBuilder()
                .recipientCustomer(customer)
                .recipientType(NotificationRecipientType.CUSTOMER)
                .build();
    }
}
