package com.drop.here.backend.drophere.drop.service;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.drop.dto.request.DropJoinRequest;
import com.drop.here.backend.drophere.drop.dto.request.DropManagementRequest;
import com.drop.here.backend.drophere.drop.dto.response.DropMembershipResponse;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.drop.entity.DropMembership;
import com.drop.here.backend.drophere.drop.enums.DropLocationType;
import com.drop.here.backend.drophere.drop.enums.DropMembershipStatus;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import com.drop.here.backend.drophere.test_data.AuthenticationDataGenerator;
import com.drop.here.backend.drophere.test_data.DropDataGenerator;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class DropMappingServiceTest {

    @InjectMocks
    private DropMappingService dropMappingService;

    @BeforeEach
    void prepare() throws IllegalAccessException {
        FieldUtils.writeDeclaredField(dropMappingService, "namePartLength", 4, true);
        FieldUtils.writeDeclaredField(dropMappingService, "randomPartLength", 6, true);
    }

    @Test
    void givenGeolocationDropManagementRequestWhenToEntityThenMap() {
        //given
        final DropManagementRequest dropManagementRequest = DropDataGenerator.dropManagementRequest(1);
        final Account account = AccountDataGenerator.companyAccount(1);
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);
        dropManagementRequest.setName("nam");

        //when
        final Drop drop = dropMappingService.toEntity(dropManagementRequest, accountAuthentication);

        //then
        assertThat(drop.getName()).isEqualTo(dropManagementRequest.getName());
        assertThat(drop.getDescription()).isEqualTo(dropManagementRequest.getDescription());
        assertThat(drop.getEstimatedRadiusMeters()).isEqualTo(dropManagementRequest.getEstimatedRadiusMeters());
        assertThat(drop.getCreatedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusMinutes(1));
        assertThat(drop.getLastUpdatedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusMinutes(1));
        assertThat(drop.getPassword()).isEqualTo(dropManagementRequest.getPassword());
        assertThat(drop.getUid()).hasSize(9);
        assertThat(drop.getUid()).startsWith("nam");
        assertThat(drop.getXCoordinate()).isEqualTo(dropManagementRequest.getXCoordinate());
        assertThat(drop.getYCoordinate()).isEqualTo(dropManagementRequest.getYCoordinate());
        assertThat(drop.getCompany()).isEqualTo(account.getCompany());
        assertThat(drop.getLocationType()).isEqualTo(DropLocationType.GEOLOCATION);
    }

    @Test
    void giveHiddenLocationDropManagementRequestWhenToEntityThenMap() {
        //given
        final DropManagementRequest dropManagementRequest = DropDataGenerator.dropManagementRequest(1);
        final Account account = AccountDataGenerator.companyAccount(1);
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);
        dropManagementRequest.setName("namek");
        dropManagementRequest.setLocationDropType(DropLocationType.HIDDEN.name());
        //when
        final Drop drop = dropMappingService.toEntity(dropManagementRequest, accountAuthentication);

        //then
        assertThat(drop.getName()).isEqualTo(dropManagementRequest.getName());
        assertThat(drop.getDescription()).isEqualTo(dropManagementRequest.getDescription());
        assertThat(drop.getEstimatedRadiusMeters()).isNull();
        assertThat(drop.getCreatedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusMinutes(1));
        assertThat(drop.getLastUpdatedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusMinutes(1));
        assertThat(drop.getPassword()).isEqualTo(dropManagementRequest.getPassword());
        assertThat(drop.getUid()).hasSize(10);
        assertThat(drop.getUid()).startsWith("name");
        assertThat(drop.getUid()).doesNotContain("namek");
        assertThat(drop.getXCoordinate()).isNull();
        assertThat(drop.getYCoordinate()).isNull();
        assertThat(drop.getCompany()).isEqualTo(account.getCompany());
        assertThat(drop.getLocationType()).isEqualTo(DropLocationType.HIDDEN);
    }

    @Test
    void givenDropAndDropManagementRequestWhenUpdateThenUpdate() {
        //given
        final DropManagementRequest dropManagementRequest = DropDataGenerator.dropManagementRequest(1);
        dropManagementRequest.setName("nam");
        final Drop drop = Drop.builder().build();

        //when
        dropMappingService.update(drop, dropManagementRequest);

        //then
        assertThat(drop.getName()).isEqualTo(dropManagementRequest.getName());
        assertThat(drop.getDescription()).isEqualTo(dropManagementRequest.getDescription());
        assertThat(drop.getEstimatedRadiusMeters()).isEqualTo(dropManagementRequest.getEstimatedRadiusMeters());
        assertThat(drop.getCreatedAt()).isNull();
        assertThat(drop.getLastUpdatedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusMinutes(1));
        assertThat(drop.getPassword()).isEqualTo(dropManagementRequest.getPassword());
        assertThat(drop.getUid()).hasSize(9);
        assertThat(drop.getUid()).startsWith("nam");
        assertThat(drop.getXCoordinate()).isEqualTo(dropManagementRequest.getXCoordinate());
        assertThat(drop.getYCoordinate()).isEqualTo(dropManagementRequest.getYCoordinate());
        assertThat(drop.getCompany()).isNull();
        assertThat(drop.getLocationType()).isEqualTo(DropLocationType.GEOLOCATION);
    }

    @Test
    void givenDropWithoutAcceptWhenCreateMembershipThenCreate() {
        //given
        final Drop drop = DropDataGenerator.drop(1, null)
                .toBuilder()
                .requiresAccept(false)
                .build();
        final Customer customer = Customer.builder().build();
        final AccountAuthentication accountAuthentication = AccountAuthentication
                .builder()
                .customer(customer)
                .build();
        final DropJoinRequest dropJoinRequest = DropJoinRequest.builder().receiveNotification(true).build();

        //when
        final DropMembership membership = dropMappingService.createMembership(drop, dropJoinRequest, accountAuthentication);

        //then
        assertThat(membership.getCreatedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusMinutes(1));
        assertThat(membership.getCustomer()).isEqualTo(customer);
        assertThat(membership.getDrop()).isEqualTo(drop);
        assertThat(membership.isReceiveNotification()).isTrue();
        assertThat(membership.getMembershipStatus()).isEqualTo(DropMembershipStatus.ACTIVE);
    }

    @Test
    void givenDropWithAcceptWhenCreateMembershipThenCreate() {
        //given
        final Drop drop = DropDataGenerator.drop(1, null)
                .toBuilder()
                .requiresAccept(true)
                .build();
        final Customer customer = Customer.builder().build();
        final AccountAuthentication accountAuthentication = AccountAuthentication
                .builder()
                .customer(customer)
                .build();
        final DropJoinRequest dropJoinRequest = DropJoinRequest.builder().receiveNotification(false).build();

        //when
        final DropMembership membership = dropMappingService.createMembership(drop, dropJoinRequest, accountAuthentication);

        //then
        assertThat(membership.getCreatedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusMinutes(1));
        assertThat(membership.getCustomer()).isEqualTo(customer);
        assertThat(membership.getDrop()).isEqualTo(drop);
        assertThat(membership.isReceiveNotification()).isFalse();
        assertThat(membership.getMembershipStatus()).isEqualTo(DropMembershipStatus.PENDING);
    }

    @Test
    void givenDropMembershipWhenToDropMembershipResponseThenMap() {
        //given
        final Company company = Company.builder().build();
        final Drop drop = DropDataGenerator.drop(1, company);
        final Customer customer = Customer.builder().build();
        final DropMembership dropMembership = DropDataGenerator.membership(drop, customer);

        //when
        final DropMembershipResponse result = dropMappingService.toDropMembershipResponse(dropMembership);

        //then
        assertThat(result.getDropMembershipStatus()).isEqualTo(dropMembership.getMembershipStatus());
    }
}