package com.drop.here.backend.drophere.spot.service;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.spot.dto.request.SpotJoinRequest;
import com.drop.here.backend.drophere.spot.dto.request.SpotManagementRequest;
import com.drop.here.backend.drophere.spot.entity.Spot;
import com.drop.here.backend.drophere.spot.entity.SpotMembership;
import com.drop.here.backend.drophere.spot.enums.SpotMembershipStatus;
import com.drop.here.backend.drophere.configuration.security.AccountAuthentication;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import com.drop.here.backend.drophere.test_data.AuthenticationDataGenerator;
import com.drop.here.backend.drophere.test_data.SpotDataGenerator;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class SpotMappingServiceTest {

    @InjectMocks
    private SpotMappingService spotMappingService;

    @BeforeEach
    void prepare() throws IllegalAccessException {
        FieldUtils.writeDeclaredField(spotMappingService, "namePartLength", 4, true);
        FieldUtils.writeDeclaredField(spotMappingService, "randomPartLength", 6, true);
    }

    @Test
    void givenSpotManagementRequestWhenToEntityThenMap() {
        //given
        final SpotManagementRequest spotManagementRequest = SpotDataGenerator.spotManagementRequest(1);
        final Account account = AccountDataGenerator.companyAccount(1);
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);
        spotManagementRequest.setName("nam");

        //when
        final Spot spot = spotMappingService.toEntity(spotManagementRequest, accountAuthentication);

        //then
        assertThat(spot.getName()).isEqualTo(spotManagementRequest.getName());
        assertThat(spot.getDescription()).isEqualTo(spotManagementRequest.getDescription());
        assertThat(spot.getEstimatedRadiusMeters()).isEqualTo(spotManagementRequest.getEstimatedRadiusMeters());
        assertThat(spot.getCreatedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusMinutes(1));
        assertThat(spot.getLastUpdatedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusMinutes(1));
        assertThat(spot.getPassword()).isEqualTo(spotManagementRequest.getPassword());
        assertThat(spot.getUid()).hasSize(9);
        assertThat(spot.getUid()).startsWith("nam");
        assertThat(spot.getXCoordinate()).isEqualTo(spotManagementRequest.getXCoordinate());
        assertThat(spot.getYCoordinate()).isEqualTo(spotManagementRequest.getYCoordinate());
        assertThat(spot.getCompany()).isEqualTo(account.getCompany());
    }

    @Test
    void givenSpotAndSpotManagementRequestWhenUpdateThenUpdate() {
        //given
        final SpotManagementRequest spotManagementRequest = SpotDataGenerator.spotManagementRequest(1);
        spotManagementRequest.setName("nam");
        final Spot spot = Spot.builder().build();

        //when
        spotMappingService.update(spot, spotManagementRequest);

        //then
        assertThat(spot.getName()).isEqualTo(spotManagementRequest.getName());
        assertThat(spot.getDescription()).isEqualTo(spotManagementRequest.getDescription());
        assertThat(spot.getEstimatedRadiusMeters()).isEqualTo(spotManagementRequest.getEstimatedRadiusMeters());
        assertThat(spot.getCreatedAt()).isNull();
        assertThat(spot.getLastUpdatedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusMinutes(1));
        assertThat(spot.getPassword()).isEqualTo(spotManagementRequest.getPassword());
        assertThat(spot.getUid()).hasSize(9);
        assertThat(spot.getUid()).startsWith("nam");
        assertThat(spot.getXCoordinate()).isEqualTo(spotManagementRequest.getXCoordinate());
        assertThat(spot.getYCoordinate()).isEqualTo(spotManagementRequest.getYCoordinate());
        assertThat(spot.getCompany()).isNull();
    }

    @Test
    void givenSpotWithoutAcceptWhenCreateMembershipThenCreate() {
        //given
        final Spot spot = SpotDataGenerator.spot(1, null)
                .toBuilder()
                .requiresAccept(false)
                .build();
        final Customer customer = Customer.builder().build();
        final AccountAuthentication accountAuthentication = AccountAuthentication
                .builder()
                .customer(customer)
                .build();
        final SpotJoinRequest spotJoinRequest = SpotJoinRequest.builder().receiveNotification(true).build();

        //when
        final SpotMembership membership = spotMappingService.createMembership(spot, spotJoinRequest, accountAuthentication);

        //then
        assertThat(membership.getCreatedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusMinutes(1));
        assertThat(membership.getCustomer()).isEqualTo(customer);
        assertThat(membership.getSpot()).isEqualTo(spot);
        assertThat(membership.isReceiveNotification()).isTrue();
        assertThat(membership.getMembershipStatus()).isEqualTo(SpotMembershipStatus.ACTIVE);
    }

    @Test
    void givenSpotWithAcceptWhenCreateMembershipThenCreate() {
        //given
        final Spot spot = SpotDataGenerator.spot(1, null)
                .toBuilder()
                .requiresAccept(true)
                .build();
        final Customer customer = Customer.builder().build();
        final AccountAuthentication accountAuthentication = AccountAuthentication
                .builder()
                .customer(customer)
                .build();
        final SpotJoinRequest spotJoinRequest = SpotJoinRequest.builder().receiveNotification(false).build();

        //when
        final SpotMembership membership = spotMappingService.createMembership(spot, spotJoinRequest, accountAuthentication);

        //then
        assertThat(membership.getCreatedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusMinutes(1));
        assertThat(membership.getCustomer()).isEqualTo(customer);
        assertThat(membership.getSpot()).isEqualTo(spot);
        assertThat(membership.isReceiveNotification()).isFalse();
        assertThat(membership.getMembershipStatus()).isEqualTo(SpotMembershipStatus.PENDING);
    }
}