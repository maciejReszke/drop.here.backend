package com.drop.here.backend.drophere.notification.service.broadcasting;

import com.drop.here.backend.drophere.notification.service.firebase.FirebaseNotificationBroadcastingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationBroadcastingServiceFactory {
    private final FirebaseNotificationBroadcastingService firebaseNotificationBroadcastingService;

    public NotificationBroadcastingService getNotificationBroadcastingService() {
        return firebaseNotificationBroadcastingService;
    }
}
