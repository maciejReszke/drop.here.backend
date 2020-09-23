package com.drop.here.backend.drophere.notification.service;

import com.drop.here.backend.drophere.notification.dto.NotificationManagementRequest;
import com.drop.here.backend.drophere.notification.dto.NotificationResponse;
import com.drop.here.backend.drophere.notification.entity.Notification;
import com.drop.here.backend.drophere.notification.enums.NotificationBroadcastingType;
import com.drop.here.backend.drophere.notification.enums.NotificationReadStatus;
import com.drop.here.backend.drophere.notification.enums.NotificationReferencedSubjectType;
import io.vavr.API;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

import static io.vavr.API.$;
import static io.vavr.API.Case;

// TODO MONO:
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
                .type(notification.getType())
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
}
