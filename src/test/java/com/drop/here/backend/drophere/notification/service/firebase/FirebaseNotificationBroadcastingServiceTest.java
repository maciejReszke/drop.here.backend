package com.drop.here.backend.drophere.notification.service.firebase;

import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.notification.entity.Notification;
import com.drop.here.backend.drophere.test_data.CustomerDataGenerator;
import com.drop.here.backend.drophere.test_data.NotificationDataGenerator;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.org.apache.commons.lang.reflect.FieldUtils;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FirebaseNotificationBroadcastingServiceTest {

    @InjectMocks
    private FirebaseNotificationBroadcastingService firebaseNotificationBroadcastingService;

    @Mock
    private FirebaseInitializationService firebaseInitializationService;

    @Mock
    private FirebaseMappingService firebaseMappingService;

    @Mock
    private FirebaseExecutorService firebaseExecutorService;

    @BeforeEach
    void prepare() throws IllegalAccessException {
        FieldUtils.writeDeclaredField(firebaseNotificationBroadcastingService, "maxBatchSize", 34, true);
    }

    @Test
    void whenGetBatchThenGet() {
        //when
        final int result = firebaseNotificationBroadcastingService.getBatchAmount();

        //then
        assertThat(result).isEqualTo(34);
    }

    @Test
    void givenNotificationsSuccessSendingWhenSendBatchThenSend() throws FirebaseMessagingException, IOException {
        //given
        final Customer customer = CustomerDataGenerator.customer(1, null);
        final Notification notification = NotificationDataGenerator.customerNotification(1, customer);
        final List<Notification> notifications = List.of(notification);

        final Message message = Message.builder().setToken("elo").build();
        doNothing().when(firebaseInitializationService).initialize();
        when(firebaseMappingService.toMessage(notification)).thenReturn(message);
        doNothing().when(firebaseExecutorService).sendAll(List.of(message));

        //when
        final boolean result = firebaseNotificationBroadcastingService.sendBatch(notifications);

        //then
        assertThat(result).isTrue();
    }

    @Test
    void givenNotificationsFailureSendingWhenSendBatchThenFalse() throws FirebaseMessagingException, IOException {
        //given
        final Customer customer = CustomerDataGenerator.customer(1, null);
        final Notification notification = NotificationDataGenerator.customerNotification(1, customer);
        final List<Notification> notifications = List.of(notification);

        final Message message = Message.builder().setToken("elo").build();
        doNothing().when(firebaseInitializationService).initialize();
        when(firebaseMappingService.toMessage(notification)).thenReturn(message);
        doThrow(new RuntimeException()).when(firebaseExecutorService).sendAll(List.of(message));

        //when
        final boolean result = firebaseNotificationBroadcastingService.sendBatch(notifications);

        //then
        assertThat(result).isFalse();
    }


}