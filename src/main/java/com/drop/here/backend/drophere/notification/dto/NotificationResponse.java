package com.drop.here.backend.drophere.notification.dto;

import com.drop.here.backend.drophere.notification.enums.NotificationBroadcastingType;
import com.drop.here.backend.drophere.notification.enums.NotificationReadStatus;
import com.drop.here.backend.drophere.notification.enums.NotificationReferencedSubjectType;
import com.drop.here.backend.drophere.notification.enums.NotificationType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class NotificationResponse {

    @ApiModelProperty(value = "Notification id", example = "Abc123")
    String id;

    @ApiModelProperty(value = "Notification title", example = "ęśąćż")
    String title;

    @ApiModelProperty(value = "Notification message", example = "Natalka kocham ci ęśąćż e mi na mordzie")
    String message;

    @ApiModelProperty(value = "Notification type", example = "TEST")
    NotificationType type;

    @ApiModelProperty(value = "Notification read status", example = "READ")
    NotificationReadStatus readStatus;

    @ApiModelProperty(value = "Notification referenced subject", example = "COMPANY")
    NotificationReferencedSubjectType referencedSubjectType;

    @ApiModelProperty(value = "Notification referenced subject id", example = "asdoiao213")
    String referencedSubjectId;

    @ApiModelProperty(value = "Notification broadcasting type", example = "COMPANY")
    NotificationBroadcastingType broadcastingType;

    @ApiModelProperty(value = "Notification broadcaster id", example = "asdsa")
    String broadcasterId;

    @ApiModelProperty(value = "Notification detailed message", example = "Keppoo")
    String detailedMessage;

    @ApiModelProperty(value = "Created at", example = "2020-04-06T11:03:32")
    String createdAt;

}