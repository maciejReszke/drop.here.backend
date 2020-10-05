package com.drop.here.backend.drophere.spot.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Value
public class SpotMembershipNotificationStatus {
    boolean prepared;
    boolean live;
    boolean finished;
    boolean delayed;
    boolean canceled;

    public static SpotMembershipNotificationStatus delayed() {
        return new SpotMembershipNotificationStatus(false, false, false, true, false);
    }

    public static SpotMembershipNotificationStatus live() {
        return new SpotMembershipNotificationStatus(false, true, false, false, false);
    }

    // TODO: 05/10/2020
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
