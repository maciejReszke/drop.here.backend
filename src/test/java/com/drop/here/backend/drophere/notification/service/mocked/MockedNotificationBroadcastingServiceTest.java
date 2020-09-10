package com.drop.here.backend.drophere.notification.service.mocked;

import com.drop.here.backend.drophere.notification.entity.Notification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MockedNotificationBroadcastingServiceTest {

    @InjectMocks
    private MockedNotificationBroadcastingService mockedNotificationBroadcastingService;

    @Test
    void givenNotificationsWhenSendBatchThenTrue() {
        //given
        final List<Notification> notifications = List.of();

        //when
        final boolean result = mockedNotificationBroadcastingService.sendBatch(notifications);

        //then
        assertThat(result).isTrue();
    }

    @Test
    void whenGetBatchAmountThenGet() {
        //when
        final int batchAmount = mockedNotificationBroadcastingService.getBatchAmount();

        //then
        assertThat(batchAmount).isEqualTo(500);
    }

}