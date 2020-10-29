package com.drop.here.backend.drophere.notification.service.token;

import com.drop.here.backend.drophere.notification.entity.Notification;
import com.drop.here.backend.drophere.notification.entity.NotificationToken;
import lombok.Value;

import java.util.Optional;

@Value
public class NotificationAndTokenWrapper {
    Notification notification;
    Optional<NotificationToken> token;
}
