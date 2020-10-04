package com.drop.here.backend.drophere.spot.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class SpotMembershipNotificationStatus {
    private final boolean prepared;
    private final boolean live;
    private final boolean finished;
    private final boolean delayed;
    private final boolean canceled;

    public static SpotMembershipNotificationStatus delayed() {
        return new SpotMembershipNotificationStatus(false, false, false, true, false);
    }

    public static SpotMembershipNotificationStatus live() {
        return new SpotMembershipNotificationStatus(false, true, false, false, false);
    }

    public static SpotMembershipNotificationStatus prepared() {
        return new SpotMembershipNotificationStatus(true, false, false, false, false);
    }

    public static SpotMembershipNotificationStatus cancelled() {
        return new SpotMembershipNotificationStatus(false, false, false, false, true);
    }

    public static SpotMembershipNotificationStatus finished() {
        return new SpotMembershipNotificationStatus(false, false, true, false, false);
    }
}
