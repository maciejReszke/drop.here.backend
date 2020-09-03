package com.drop.here.backend.drophere.test_data;

import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.drop.dto.request.DropManagementRequest;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.drop.entity.DropMembership;
import com.drop.here.backend.drophere.drop.enums.DropLocationType;
import com.drop.here.backend.drophere.drop.enums.DropMembershipStatus;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class DropDataGenerator {
    public Drop drop(int i, Company company) {
        return Drop.builder()
                .company(company)
                .description("description" + i)
                .estimatedRadiusMeters(600)
                .hidden(false)
                .name("dropName" + i)
                .locationType(DropLocationType.GEOLOCATION)
                .password("password")
                .requiresAccept(false)
                .requiresPassword(true)
                .xCoordinate(54.423569)
                .yCoordinate(18.564037)
                .uid("dropUid" + i)
                .lastUpdatedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public DropManagementRequest dropManagementRequest(int i) {
        return DropManagementRequest.builder()
                .description("description" + i)
                .estimatedRadiusMeters(600)
                .hidden(false)
                .locationDropType(DropLocationType.GEOLOCATION.name())
                .name("dropName" + i)
                .password("password" + i)
                .requiresAccept(false)
                .requiresPassword(true)
                .xCoordinate(55.423569)
                .yCoordinate(17.564037)
                .build();
    }

    public DropMembership membership(Drop drop, Customer customer) {
        return DropMembership.builder()
                .customer(customer)
                .membershipStatus(DropMembershipStatus.ACTIVE)
                .drop(drop)
                .receiveNotification(false)
                .lastUpdatedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
    }
}
