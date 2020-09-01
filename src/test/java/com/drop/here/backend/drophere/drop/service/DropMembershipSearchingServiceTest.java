package com.drop.here.backend.drophere.drop.service;

import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.drop.dto.response.DropCompanyMembershipResponse;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.drop.entity.DropMembership;
import com.drop.here.backend.drophere.drop.enums.DropMembershipStatus;
import com.drop.here.backend.drophere.drop.repository.DropMembershipRepository;
import com.drop.here.backend.drophere.test_data.DropDataGenerator;
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
class DropMembershipSearchingServiceTest {

    @InjectMocks
    private DropMembershipSearchingService dropMembershipSearchingService;

    @Mock
    private DropMembershipRepository dropMembershipRepository;

    @Test
    void givenDropWhenFindMembershipsThenFind() {
        //given
        final Drop drop = Drop.builder().build();
        final String desiredCustomerSubstring = "mac";
        final DropMembershipStatus dropMembershipStatus = DropMembershipStatus.ACTIVE;
        final Pageable pageable = Pageable.unpaged();
        final Customer customer = Customer.builder().id(5L).firstName("anal").lastName("fisting").build();

        final DropMembership membership = DropDataGenerator.membership(drop, customer);
        final Page<DropMembership> dropCompanyMembershipResponses = new PageImpl<>(List.of(membership));
        when(dropMembershipRepository.findMembershipsWithCustomers(drop, desiredCustomerSubstring + '%', new DropMembershipStatus[]{dropMembershipStatus}, pageable))
                .thenReturn(dropCompanyMembershipResponses);

        //when
        final Page<DropCompanyMembershipResponse> result = dropMembershipSearchingService.findMemberships(drop, desiredCustomerSubstring, dropMembershipStatus.name(), pageable);

        //then
        assertThat(result.getContent()).hasSize(1);
        final DropCompanyMembershipResponse response = result.getContent().get(0);
        assertThat(response.getCustomerId()).isEqualTo(customer.getId());
        assertThat(response.getFirstName()).isEqualTo(customer.getFirstName());
        assertThat(response.getLastName()).isEqualTo(customer.getLastName());
        assertThat(response.getMembershipStatus()).isEqualTo(membership.getMembershipStatus());
    }
}