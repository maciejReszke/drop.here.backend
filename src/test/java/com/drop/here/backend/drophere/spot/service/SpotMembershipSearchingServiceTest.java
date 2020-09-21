package com.drop.here.backend.drophere.spot.service;

import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.spot.dto.response.SpotCompanyMembershipResponse;
import com.drop.here.backend.drophere.spot.entity.Spot;
import com.drop.here.backend.drophere.spot.entity.SpotMembership;
import com.drop.here.backend.drophere.spot.enums.SpotMembershipStatus;
import com.drop.here.backend.drophere.spot.repository.SpotMembershipRepository;
import com.drop.here.backend.drophere.test_data.SpotDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpotMembershipSearchingServiceTest {

    @InjectMocks
    private SpotMembershipSearchingService spotMembershipSearchingService;

    @Mock
    private SpotMembershipRepository spotMembershipRepository;

    @Test
    void givenSpotWhenFindMembershipsThenFind() {
        //given
        final Spot spot = Spot.builder().build();
        final String desiredCustomerSubstring = "mac";
        final SpotMembershipStatus spotMembershipStatus = SpotMembershipStatus.ACTIVE;
        final Pageable pageable = Pageable.unpaged();
        final Customer customer = Customer.builder().id(5L).firstName("anal").lastName("fisting").build();

        final SpotMembership membership = SpotDataGenerator.membership(spot, customer);
        final Page<SpotMembership> dropCompanyMembershipResponses = new PageImpl<>(List.of(membership));
        when(spotMembershipRepository.findMembershipsWithCustomers(spot, desiredCustomerSubstring + '%', new SpotMembershipStatus[]{spotMembershipStatus}, pageable))
                .thenReturn(dropCompanyMembershipResponses);

        //when
        final Page<SpotCompanyMembershipResponse> result = spotMembershipSearchingService.findMemberships(spot, desiredCustomerSubstring, spotMembershipStatus.name(), pageable);

        //then
        assertThat(result.getContent()).hasSize(1);
        final SpotCompanyMembershipResponse response = result.getContent().get(0);
        assertThat(response.getCustomerId()).isEqualTo(customer.getId());
        assertThat(response.getFirstName()).isEqualTo(customer.getFirstName());
        assertThat(response.getLastName()).isEqualTo(customer.getLastName());
        assertThat(response.getMembershipStatus()).isEqualTo(membership.getMembershipStatus());
    }
}