package com.drop.here.backend.drophere.notification.service.firebase;

import com.drop.here.backend.drophere.notification.configuration.GoogleCredentialsConfiguration;
import com.drop.here.backend.drophere.notification.configuration.GoogleCredentialsRequest;
import com.drop.here.backend.drophere.notification.entity.Notification;
import com.drop.here.backend.drophere.notification.entity.NotificationJob;
import com.drop.here.backend.drophere.notification.entity.NotificationToken;
import com.drop.here.backend.drophere.notification.enums.NotificationReferencedSubjectType;
import com.drop.here.backend.drophere.notification.service.broadcasting.NotificationBroadcastingUtilService;
import com.google.firebase.messaging.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FirebaseMappingServiceTest {

    @InjectMocks
    private FirebaseMappingService firebaseMappingService;

    @Mock
    private GoogleCredentialsConfiguration googleCredentialsConfiguration;

    @Mock
    private NotificationBroadcastingUtilService notificationBroadcastingUtilService;

    @Test
    void whenPrepareCredentialsRequest() {
        //when
        final GoogleCredentialsRequest prepare = firebaseMappingService.prepareCredentialsRequest();

        //then
        assertThat(prepare).isNotNull();
    }

    @Test
    void givenNotificationWhenToMessageThenMap() {
        //given
        final Notification notification = Notification.builder().referencedSubjectType(NotificationReferencedSubjectType.EMPTY)
                .referencedSubjectId("sid").build();
        final NotificationToken token = NotificationToken.builder().token("123").build();
        final NotificationJob notificationJob = NotificationJob.builder().notification(notification).notificationToken(token).build();

        when(notificationBroadcastingUtilService.getImageUrl(notification)).thenReturn("url123");

        //when
        final Message message = firebaseMappingService.toMessage(notificationJob);

        //then
        assertThat(message).isNotNull();
    }

}