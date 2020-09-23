package com.drop.here.backend.drophere.notification.service.broadcasting;

import com.drop.here.backend.drophere.notification.service.firebase.FirebaseNotificationBroadcastingService;
import com.drop.here.backend.drophere.notification.service.mocked.MockedNotificationBroadcastingService;
import io.vavr.API;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static io.vavr.API.$;
import static io.vavr.API.Case;

// TODO MONO:
@Service
@RequiredArgsConstructor
public class NotificationBroadcastingServiceFactory {
    private final FirebaseNotificationBroadcastingService firebaseNotificationBroadcastingService;
    private final MockedNotificationBroadcastingService mockedNotificationBroadcastingService;

    @Value("${notification.broadcasting.implementation}")
    private String implementation;

    private static final String FIREBASE = "FIREBASE";
    private static final String MOCKED = "MOCKED";

    public NotificationBroadcastingService getNotificationBroadcastingService() {
        return API.Match(implementation).of(
                Case($(FIREBASE), firebaseNotificationBroadcastingService),
                Case($(MOCKED), mockedNotificationBroadcastingService)
        );
    }
}
