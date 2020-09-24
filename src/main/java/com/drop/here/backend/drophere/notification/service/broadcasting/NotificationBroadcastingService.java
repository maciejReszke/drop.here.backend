package com.drop.here.backend.drophere.notification.service.broadcasting;

import com.drop.here.backend.drophere.notification.entity.NotificationJob;
import reactor.core.publisher.Mono;

import java.util.List;

public interface NotificationBroadcastingService {
    Mono<Boolean> sendBatch(List<NotificationJob> notifications);

    int getBatchAmount();
}
