package com.drop.here.backend.drophere.test_data;

import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.notification.entity.Notification;
import com.drop.here.backend.drophere.notification.enums.NotificationBroadcastingStatus;
import com.drop.here.backend.drophere.notification.enums.NotificationBroadcastingType;
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

    private Notification baseNotification(int i) {
        return Notification.builder()
                .broadcastingStatus(NotificationBroadcastingStatus.NOT_SENT)
                .createdAt(LocalDateTime.now())
                .message("Message" + i)
                .readStatus(NotificationReadStatus.UNREAD)
                .title("title" + i)
                .type(NotificationType.TEST)
                .referencedSubjectType(NotificationReferencedSubjectType.EMPTY)
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
