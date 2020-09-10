package com.drop.here.backend.drophere.notification.service.broadcasting;

import com.drop.here.backend.drophere.notification.entity.NotificationJob;

import java.util.List;

public interface NotificationBroadcastingService {
    boolean sendBatch(List<NotificationJob> notifications);

    int getBatchAmount();
}
