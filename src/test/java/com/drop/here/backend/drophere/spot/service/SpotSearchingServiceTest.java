package com.drop.here.backend.drophere.spot.service;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.drop.dto.DropCustomerSpotResponse;
import com.drop.here.backend.drophere.drop.service.DropService;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import com.drop.here.backend.drophere.spot.dto.response.SpotDetailedCustomerResponse;
import com.drop.here.backend.drophere.spot.entity.Spot;
import com.drop.here.backend.drophere.spot.entity.SpotMembership;
import com.drop.here.backend.drophere.spot.repository.SpotRepository;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import com.drop.here.backend.drophere.test_data.AuthenticationDataGenerator;
import com.drop.here.backend.drophere.test_data.CustomerDataGenerator;
import com.drop.here.backend.drophere.test_data.SpotDataGenerator;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpotSearchingServiceTest {
    @InjectMocks
    private SpotSearchingService spotSearchingService;

    @Mock
    private SpotMembershipSearchingService spotMembershipSearchingService;

    @Mock
    private SpotRepository spotRepository;

    @Mock
    private DropService dropService;

    @BeforeEach
    void prepare() throws IllegalAccessException {
        FieldUtils.writeDeclaredField(spotSearchingService, "spotResponseDropsForDays", 7, true);
    }

    @Test
    void givenExistingSpotAndMembershipWhenFindSpotThenFind() {
        //given
        final String spotUid = "spotUid";
        final Account account = AccountDataGenerator.customerAccount(1);
        final Customer customer = CustomerDataGenerator.customer(1, account);
        final Company company = Company.builder().build();
        final Spot spot = SpotDataGenerator.spot(1, company);
        account.setCustomer(customer);
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);
        final SpotMembership membership = SpotDataGenerator.membership(spot, customer);
        final List<DropCustomerSpotResponse> drops = List.of();

        when(spotRepository.findPrivilegedSpot(spotUid, customer))
                .thenReturn(Optional.of(spot));
        when(spotMembershipSearchingService.findMembership(spot, customer)).thenReturn(Optional.of(membership));
        when(dropService.findDrops(spot, LocalDate.now().atStartOfDay(), LocalDate.now().atStartOfDay().plusDays(7))).thenReturn(drops);

        //when
        final SpotDetailedCustomerResponse result = spotSearchingService.findSpot(spotUid, accountAuthentication);

        //then
        assertThat(result.getDrops()).isEqualTo(drops);
        assertThat(result.getSpot().getCompanyName()).isEqualTo(spot.getCompany().getName());
        assertThat(result.getSpot().getCompanyUid()).isEqualTo(spot.getCompany().getUid());
        assertThat(result.getSpot().getDescription()).isEqualTo(spot.getDescription());
        assertThat(result.getSpot().getEstimatedRadiusMeters()).isEqualTo(spot.getEstimatedRadiusMeters());
        assertThat(result.getSpot().getName()).isEqualTo(spot.getName());
        assertThat(result.getSpot().getUid()).isEqualTo(spot.getUid());
        assertThat(result.getSpot().getXCoordinate()).isEqualTo(spot.getXCoordinate());
        assertThat(result.getSpot().getYCoordinate()).isEqualTo(spot.getYCoordinate());
        assertThat(result.getSpot().getMembershipStatus()).isEqualTo(membership.getMembershipStatus());
    }

    @Test
    void givenExistingSpotNotMembershipWhenFindSpotThenFind() {
        //given
        final String spotUid = "spotUid";
        final Account account = AccountDataGenerator.customerAccount(1);
        final Customer customer = CustomerDataGenerator.customer(1, account);
        final Company company = Company.builder().build();
        final Spot spot = SpotDataGenerator.spot(1, company);
        account.setCustomer(customer);
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);
        final List<DropCustomerSpotResponse> drops = List.of();

        when(spotRepository.findPrivilegedSpot(spotUid, customer))
                .thenReturn(Optional.of(spot));
        when(spotMembershipSearchingService.findMembership(spot, customer)).thenReturn(Optional.empty());
        when(dropService.findDrops(spot, LocalDate.now().atStartOfDay(), LocalDate.now().atStartOfDay().plusDays(7))).thenReturn(drops);

        //when
        final SpotDetailedCustomerResponse result = spotSearchingService.findSpot(spotUid, accountAuthentication);

        //then
        assertThat(result.getDrops()).isEqualTo(drops);
        assertThat(result.getSpot().getCompanyName()).isEqualTo(spot.getCompany().getName());
        assertThat(result.getSpot().getCompanyUid()).isEqualTo(spot.getCompany().getUid());
        assertThat(result.getSpot().getDescription()).isEqualTo(spot.getDescription());
        assertThat(result.getSpot().getEstimatedRadiusMeters()).isEqualTo(spot.getEstimatedRadiusMeters());
        assertThat(result.getSpot().getName()).isEqualTo(spot.getName());
        assertThat(result.getSpot().getUid()).isEqualTo(spot.getUid());
        assertThat(result.getSpot().getXCoordinate()).isEqualTo(spot.getXCoordinate());
        assertThat(result.getSpot().getYCoordinate()).isEqualTo(spot.getYCoordinate());
        assertThat(result.getSpot().getMembershipStatus()).isNull();
    }

    @Test
    void givenNotExistingSpotWhenFindSpotThenThrowException() {
        //given
        final String spotUid = "spotUid";
        final Account account = AccountDataGenerator.customerAccount(1);
        final Customer customer = CustomerDataGenerator.customer(1, account);
        account.setCustomer(customer);
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);

        when(spotRepository.findPrivilegedSpot(spotUid, customer)).thenReturn(Optional.empty());

        //when
        final Throwable throwable = catchThrowable(() -> spotSearchingService.findSpot(spotUid, accountAuthentication));

        //then
        assertThat(throwable).isInstanceOf(RestEntityNotFoundException.class);
    }

}