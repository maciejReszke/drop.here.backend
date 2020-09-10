package com.drop.here.backend.drophere.notification.service.mocked;

import com.drop.here.backend.drophere.notification.entity.NotificationJob;
import com.drop.here.backend.drophere.notification.service.broadcasting.NotificationBroadcastingService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MockedNotificationBroadcastingService implements NotificationBroadcastingService {

    @Override
    public boolean sendBatch(List<NotificationJob> notifications) {
        return true;
    }

    @Override
    public int getBatchAmount() {
        return 500;
    }
}
