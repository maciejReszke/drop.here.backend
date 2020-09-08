package com.drop.here.backend.drophere.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@ConditionalOnProperty(value = "notification.broadcasting.scheduling.enabled", havingValue = "true")
@Slf4j
public class NotificationBroadcastingScheduler {
    private final NotificationService notificationService;

    // TODO: 08/09/2020 test + openshift (secrety i url do serwera do zdjec notyfiakcji)
    @Scheduled(cron = "${notification.broadcasting.scheduling.cron}")
    @SchedulerLock(name = "notificationBroadcastingSchedule",
            lockAtMostFor = "${notification.broadcasting.scheduling.lock}",
            lockAtLeastFor = "${notification.broadcasting.scheduling.lock}")
    public void broadcastNotifications() {
        notificationService.sendNotifications();
    }

}
