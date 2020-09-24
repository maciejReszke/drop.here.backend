package com.drop.here.backend.drophere.notification.service;

import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.common.exceptions.RestIllegalRequestValueException;
import com.drop.here.backend.drophere.notification.dto.NotificationManagementRequest;
import com.drop.here.backend.drophere.notification.enums.NotificationReadStatus;
import io.vavr.control.Try;
import org.springframework.stereotype.Service;

@Service
public class NotificationValidationService {

    public void validateUpdateNotificationRequest(NotificationManagementRequest notificationManagementRequest) {
        Try.of(() -> NotificationReadStatus.valueOf(notificationManagementRequest.getReadStatus()))
                .getOrElseThrow(() -> new RestIllegalRequestValueException(String.format(
                        "Notification read status %s is invalid", notificationManagementRequest.getReadStatus()),
                        RestExceptionStatusCode.NOTIFICATION_UPDATE_INVALID_READ_STATUS
                ));
    }
}
