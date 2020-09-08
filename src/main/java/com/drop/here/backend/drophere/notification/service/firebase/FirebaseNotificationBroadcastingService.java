package com.drop.here.backend.drophere.notification.service.firebase;

import com.drop.here.backend.drophere.notification.entity.Notification;
import com.drop.here.backend.drophere.notification.service.broadcasting.NotificationBroadcastingService;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FirebaseNotificationBroadcastingService implements NotificationBroadcastingService {
    private final FirebaseInitializationService firebaseInitializationService;
    private final FirebaseMappingService firebaseMappingService;
    private final FirebaseExecutorService firebaseExecutorService;

    @Value("${notification.firebase.maxBatchSize}")
    private int maxBatchSize;

    @Override
    public boolean sendBatch(List<Notification> notifications) {
        try {
            firebaseInitializationService.initialize();

            final List<Message> messages = notifications.stream()
                    .map(firebaseMappingService::toMessage)
                    .collect(Collectors.toList());

            firebaseExecutorService.sendAll(messages);

            return true;
        } catch (Exception e) {
            log.info("Exception occurred during sending batch {} of notifications {}", notifications.size(), e.getMessage());
            return false;
        }
    }

    @Override
    public int getBatchAmount() {
        return maxBatchSize;
    }
}
