package com.drop.here.backend.drophere.notification.service.firebase;

import com.drop.here.backend.drophere.notification.configuration.GoogleCredentialsConfiguration;
import com.drop.here.backend.drophere.notification.configuration.GoogleCredentialsRequest;
import com.drop.here.backend.drophere.notification.entity.Notification;
import com.drop.here.backend.drophere.notification.service.NotificationBroadcastingUtilService;
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

    public Message toMessage(Notification notification) {
        return Message.builder()
                .setNotification(com.google.firebase.messaging.Notification.builder()
                        .setImage(notificationBroadcastingUtilService.getImageUrl(notification))
                        .setTitle(notification.getTitle())
                        .setBody(notification.getMessage())
                        .build())
                .setToken(notificationBroadcastingUtilService.getToken(notification))
                .build();
    }
}