package com.drop.here.backend.drophere.notification.service.firebase;

import com.drop.here.backend.drophere.notification.entity.Notification;
import com.drop.here.backend.drophere.notification.entity.NotificationJob;
import com.drop.here.backend.drophere.notification.service.broadcasting.NotificationBroadcastingUtilService;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FirebaseMappingService {
    private final NotificationBroadcastingUtilService notificationBroadcastingUtilService;

    @Value("${firebase.notifications.referenced_subject_type_property_name}")
    private String referencedSubjectTypePropertyName;

    @Value("${firebase.notifications.referenced_subject_id_property_name}")
    private String referencedSubjectIdPropertyName;

    @Value("${firebase.notifications.firebase_click_action}")
    private String firebaseClickAction;

    public Message toMessage(NotificationJob notificationJob) {
        final Notification notification = notificationJob.getNotification();
        return Message.builder()
                .setNotification(com.google.firebase.messaging.Notification.builder()
                        .setImage(notificationBroadcastingUtilService.getImageUrl(notification))
                        .setTitle(notification.getTitle())
                        .setBody(notification.getMessage())
                        .build())
                .setToken(notificationJob.getNotificationToken().getToken())
                .putData(referencedSubjectTypePropertyName, notification.getReferencedSubjectType().name())
                .putData(referencedSubjectIdPropertyName, notification.getReferencedSubjectId())
                .setAndroidConfig(AndroidConfig.builder()
                        .setNotification(AndroidNotification.builder()
                                .setClickAction(firebaseClickAction)
                                .build())
                        .build())
                .setApnsConfig(ApnsConfig.builder()
                        .setAps(Aps.builder().setCategory(notification.getReferencedSubjectType().name())
                                .build())
                        .build())
                .build();
    }
}
