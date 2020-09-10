package com.drop.here.backend.drophere.notification.service.broadcasting;

import com.drop.here.backend.drophere.notification.service.firebase.FirebaseNotificationBroadcastingService;
import com.drop.here.backend.drophere.notification.service.mocked.MockedNotificationBroadcastingService;
import org.apache.commons.lang3.reflect.FieldUtils;
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

    @Mock
    private MockedNotificationBroadcastingService mockedNotificationBroadcastingService;

    @Test
    void givenFirebasePropertyWhenGetNotificationBroadcastingServiceThenGetFirebase() throws IllegalAccessException {
        //given
        FieldUtils.writeDeclaredField(notificationBroadcastingServiceFactory, "implementation", "FIREBASE", true);
        //when

        final NotificationBroadcastingService notificationBroadcastingService = notificationBroadcastingServiceFactory.getNotificationBroadcastingService();

        //then
        assertThat(notificationBroadcastingService).isEqualTo(firebaseNotificationBroadcastingService);
    }

    @Test
    void givenMockedPropertyWhenGetNotificationBroadcastingServiceThenGetMocked() throws IllegalAccessException {
        //given
        FieldUtils.writeDeclaredField(notificationBroadcastingServiceFactory, "implementation", "MOCKED", true);

        //when
        final NotificationBroadcastingService notificationBroadcastingService = notificationBroadcastingServiceFactory.getNotificationBroadcastingService();

        //then
        assertThat(notificationBroadcastingService).isEqualTo(mockedNotificationBroadcastingService);
    }

}