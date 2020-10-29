package com.drop.here.backend.drophere.notification.service.firebase;

import com.drop.here.backend.drophere.notification.configuration.GoogleCredentialsConfiguration;
import com.drop.here.backend.drophere.notification.configuration.GoogleCredentialsRequest;
import com.drop.here.backend.drophere.notification.entity.Notification;
import com.drop.here.backend.drophere.notification.entity.NotificationJob;
import com.drop.here.backend.drophere.notification.service.broadcasting.NotificationBroadcastingUtilService;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FirebaseMappingService {
    private final GoogleCredentialsConfiguration googleCredentialsConfiguration;
    private final NotificationBroadcastingUtilService notificationBroadcastingUtilService;

    public GoogleCredentialsRequest prepareCredentialsRequest() {
        return GoogleCredentialsRequest.builder()
                .type(googleCredentialsConfiguration.getType())
                .projectId(googleCredentialsConfiguration.getProjectId())
                .privateKey(googleCredentialsConfiguration.getPrivateKey())
                .privateKeyId(googleCredentialsConfiguration.getPrivateKeyId())
                .clientEmail(googleCredentialsConfiguration.getClientEmail())
                .clientId(googleCredentialsConfiguration.getClientId())
                .authUri(googleCredentialsConfiguration.getAuthUri())
                .tokenUri(googleCredentialsConfiguration.getTokenUri())
                .authProviderX509CertUrl(googleCredentialsConfiguration.getAuthProviderX509CertUrl())
                .clientX509CertUrl(googleCredentialsConfiguration.getClientX509CertUrl())
                .build();
    }

    public Message toMessage(NotificationJob notificationJob) {
        final Notification notification = notificationJob.getNotification();
        return Message.builder()
                .setNotification(com.google.firebase.messaging.Notification.builder()
                        .setImage(notificationBroadcastingUtilService.getImageUrl(notification))
                        .setTitle(notification.getTitle())
                        .setBody(notification.getMessage())
                        .build())
                .setToken(notificationJob.getNotificationToken().getToken())
                .putData(notification.getReferencedSubjectType().name(), notification.getReferencedSubjectId())
                .setAndroidConfig(AndroidConfig.builder()
                        .setNotification(AndroidNotification.builder()
                                .setClickAction(notification.getReferencedSubjectType().name())
                                .build())
                        .build())
                .setApnsConfig(ApnsConfig.builder()
                        .setAps(Aps.builder().setCategory(notification.getReferencedSubjectType().name())
                                .build())
                        .build())
                .build();
    }
}
