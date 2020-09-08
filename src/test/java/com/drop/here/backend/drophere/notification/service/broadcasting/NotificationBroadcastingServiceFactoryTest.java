package com.drop.here.backend.drophere.notification.service.broadcasting;

import com.drop.here.backend.drophere.notification.service.firebase.FirebaseNotificationBroadcastingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class NotificationBroadcastingServiceFactoryTest {

    @InjectMocks
    private NotificationBroadcastingServiceFactory notificationBroadcastingServiceFactory;

    @Mock
    private FirebaseNotificationBroadcastingService firebaseNotificationBroadcastingService;

    @Test
    void whenGetNotificationBroadcastingServiceThenGetFirebase() {
        //when

        final NotificationBroadcastingService notificationBroadcastingService = notificationBroadcastingServiceFactory.getNotificationBroadcastingService();

        //then
        assertThat(notificationBroadcastingService).isEqualTo(firebaseNotificationBroadcastingService);
    }

}