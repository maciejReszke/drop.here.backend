package com.drop.here.backend.drophere.test_data;

import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.spot.dto.request.SpotManagementRequest;
import com.drop.here.backend.drophere.spot.entity.Spot;
import com.drop.here.backend.drophere.spot.entity.SpotMembership;
import com.drop.here.backend.drophere.spot.enums.SpotMembershipStatus;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class SpotDataGenerator {
    public Spot spot(int i, Company company) {
        return Spot.builder()
                .company(company)
                .description("description" + i)
                .estimatedRadiusMeters(600)
                .hidden(false)
                .name("spotName" + i)
                .password("password")
                .requiresAccept(false)
                .requiresPassword(true)
                .xCoordinate(54.423569)
                .yCoordinate(18.564037)
                .uid("spotUid" + i)
                .lastUpdatedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public SpotManagementRequest spotManagementRequest(int i) {
        return SpotManagementRequest.builder()
                .description("description" + i)
                .estimatedRadiusMeters(600)
                .hidden(false)
                .name("spotName" + i)
                .password("password" + i)
                .requiresAccept(false)
                .requiresPassword(true)
                .xCoordinate(55.423569)
                .yCoordinate(17.564037)
                .build();
    }

    public SpotMembership membership(Spot spot, Customer customer) {
        return SpotMembership.builder()
                .customer(customer)
                .membershipStatus(SpotMembershipStatus.ACTIVE)
                .spot(spot)
                .receiveFinishedNotifications(false)
                .receiveLiveNotifications(false)
                .receiveDelayedNotifications(true)
                .receivePreparedNotifications(false)
                .receiveCancelledNotifications(true)
                .lastUpdatedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
    }
}
