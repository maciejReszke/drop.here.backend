package com.drop.here.backend.drophere.notification.service.firebase;

import com.drop.here.backend.drophere.notification.entity.NotificationJob;
import com.drop.here.backend.drophere.notification.service.broadcasting.NotificationBroadcastingService;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FirebaseNotificationBroadcastingService implements NotificationBroadcastingService {
    private final FirebaseInitializationService firebaseInitializationService;
    private final FirebaseMappingService firebaseMappingService;
    private final FirebaseExecutorService firebaseExecutorService;

    @Value("${notification.firebase.max_batch_size}")
    private int maxBatchSize;

    @Override
    public Mono<Boolean> sendBatch(List<NotificationJob> notifications) {
        return Mono.just("Operation!")
                .publishOn(Schedulers.single())
                .flatMap(ignore -> initialize(notifications))
                .filter(result -> result)
                .map(ignore -> mapMessages(notifications))
                .flatMap(messages -> send(notifications, messages))
                .switchIfEmpty(Mono.just(false));
    }

    private Mono<Boolean> send(List<NotificationJob> notifications, List<Message> messages) {
        try {
            return firebaseExecutorService.sendAll(messages).thenReturn(true);
        } catch (Exception e) {
            log.error("Exception occurred during sending batch {} of notifications {}", notifications.size(), e.getMessage());
            return Mono.just(false);
        }
    }

    private List<Message> mapMessages(List<NotificationJob> notifications) {
        return notifications.stream()
                .map(firebaseMappingService::toMessage)
                .collect(Collectors.toList());
    }

    private Mono<Boolean> initialize(List<NotificationJob> notifications) {
        try {
            return firebaseInitializationService.initialize().thenReturn(true);
        } catch (Exception e) {
            log.error("Exception occurred during sending batch {} of notifications {}", notifications.size(), e.getMessage());
            return Mono.just(false);
        }
    }

    @Override
    public int getBatchAmount() {
        return maxBatchSize;
    }
}
