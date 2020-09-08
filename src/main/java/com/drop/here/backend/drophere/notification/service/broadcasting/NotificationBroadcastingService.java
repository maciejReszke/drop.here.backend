package com.drop.here.backend.drophere.notification.service.broadcasting;

import com.drop.here.backend.drophere.notification.entity.Notification;

import java.util.List;

public interface NotificationBroadcastingService {
    boolean sendBatch(List<Notification> notifications);

    int getBatchAmount();
}
