package com.drop.here.backend.drophere.notification.service;

import com.drop.here.backend.drophere.notification.dto.NotificationCreationRequest;
import com.drop.here.backend.drophere.notification.dto.NotificationManagementRequest;
import com.drop.here.backend.drophere.notification.dto.NotificationResponse;
import com.drop.here.backend.drophere.notification.entity.Notification;
import com.drop.here.backend.drophere.notification.entity.NotificationJob;
import com.drop.here.backend.drophere.notification.entity.NotificationToken;
import com.drop.here.backend.drophere.notification.enums.NotificationBroadcastingType;
import com.drop.here.backend.drophere.notification.enums.NotificationReadStatus;
import com.drop.here.backend.drophere.notification.enums.NotificationRecipientType;
import com.drop.here.backend.drophere.notification.enums.NotificationReferencedSubjectType;
import io.vavr.API;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.vavr.API.$;
import static io.vavr.API.Case;

@Service
public class NotificationMappingService {

    private static final String SYSTEM_ID = "SYSTEM";

    public NotificationResponse toNotificationResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .createdAt(notification.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .detailedMessage(notification.getDetailedMessage())
                .message(notification.getMessage())
                .title(notification.getTitle())
                .readStatus(notification.getReadStatus())
                .category(notification.getCategory())
                .referencedSubjectType(notification.getReferencedSubjectType())
                .referencedSubjectId(getReferencedSubjectId(notification))
                .broadcastingType(notification.getBroadcastingType())
                .broadcasterId(getBroadcasterId(notification))
                .build();
    }

    private String getBroadcasterId(Notification notification) {
        return API.Match(notification.getBroadcastingType()).of(
                Case($(NotificationBroadcastingType.COMPANY), () -> notification.getBroadcastingCompany().getId().toString()),
                Case($(NotificationBroadcastingType.CUSTOMER), () -> notification.getBroadcastingCustomer().getId().toString()),
                Case($(NotificationBroadcastingType.SYSTEM), () -> SYSTEM_ID)
        );
    }

    private String getReferencedSubjectId(Notification notification) {
        return API.Match(notification.getReferencedSubjectType()).of(
                Case($(NotificationReferencedSubjectType.EMPTY), () -> SYSTEM_ID)
        );
    }

    public void update(Notification notification, NotificationManagementRequest notificationManagementRequest) {
        notification.setReadStatus(NotificationReadStatus.valueOf(notificationManagementRequest.getReadStatus()));
    }

    public List<Notification> toNotifications(NotificationCreationRequest request) {
        final Notification baseNotification = Notification.builder()
                .title(request.getTitle())
                .message(request.getMessage())
                .detailedMessage(request.getDetailedMessage())
                .type(request.getNotificationType())
                .category(request.getNotificationCategory())
                .broadcastingType(request.getBroadcastingType())
                .broadcastingCustomer(request.getBroadcastingType() == NotificationBroadcastingType.CUSTOMER ? request.getBroadcastingCustomer() : null)
                .broadcastingCompany(request.getBroadcastingType() == NotificationBroadcastingType.COMPANY ? request.getBroadcastingCompany() : null)
                .createdAt(LocalDateTime.now())
                .referencedSubjectId(request.getReferencedSubjectId())
                .referencedSubjectType(request.getReferencedSubjectType())
                .readStatus(NotificationReadStatus.UNREAD)
                .build();

        final List<Notification> toAccountProfileNotifications = getToAccountProfileNotifications(request, baseNotification);
        final List<Notification> toCompanyNotifications = getToCompanyNotifications(request, baseNotification);
        final List<Notification> toCustomerNotifications = getToCustomerNotifications(request, baseNotification);

        return ListUtils.union(ListUtils.union(toAccountProfileNotifications, toCompanyNotifications), toCustomerNotifications);
    }

    private List<Notification> getToAccountProfileNotifications(NotificationCreationRequest request, Notification baseNotification) {
        return getToRecipientsNotifications(request.getRecipientAccountProfiles(), profile -> baseNotification
                .toBuilder()
                .recipientType(NotificationRecipientType.COMPANY_PROFILE)
                .recipientAccountProfile(profile)
                .build());
    }

    private <T> List<Notification> getToRecipientsNotifications(List<T> recipients, Function<T, Notification> notificationCreator) {
        return CollectionUtils.emptyIfNull(recipients)
                .stream()
                .map(notificationCreator)
                .collect(Collectors.toList());
    }

    private List<Notification> getToCompanyNotifications(NotificationCreationRequest request, Notification baseNotification) {
        return getToRecipientsNotifications(request.getRecipientCompanies(), company -> baseNotification
                .toBuilder()
                .recipientType(NotificationRecipientType.COMPANY)
                .recipientCompany(company)
                .build());
    }

    private List<Notification> getToCustomerNotifications(NotificationCreationRequest request, Notification baseNotification) {
        return getToRecipientsNotifications(request.getRecipientCustomers(), customer -> baseNotification
                .toBuilder()
                .recipientType(NotificationRecipientType.CUSTOMER)
                .recipientCustomer(customer)
                .build());
    }

    public NotificationJob toNotificationJob(Notification notification, NotificationToken notificationToken) {
        return NotificationJob.builder()
                .notification(notification)
                .notificationToken(notificationToken)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
