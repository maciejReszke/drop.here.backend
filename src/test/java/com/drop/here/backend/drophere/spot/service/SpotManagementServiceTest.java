package com.drop.here.backend.drophere.spot.service;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.common.rest.ResourceOperationStatus;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.spot.dto.SpotCompanyMembershipManagementRequest;
import com.drop.here.backend.drophere.spot.dto.request.SpotManagementRequest;
import com.drop.here.backend.drophere.spot.dto.response.SpotCompanyMembershipResponse;
import com.drop.here.backend.drophere.spot.dto.response.SpotCompanyResponse;
import com.drop.here.backend.drophere.spot.entity.Spot;
import com.drop.here.backend.drophere.spot.repository.SpotRepository;
import com.drop.here.backend.drophere.configuration.security.AccountAuthentication;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import com.drop.here.backend.drophere.test_data.AuthenticationDataGenerator;
import com.drop.here.backend.drophere.test_data.SpotDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpotManagementServiceTest {

    @InjectMocks
    private SpotManagementService spotManagementService;

    @Mock
    private SpotMappingService spotMappingService;

    @Mock
    private SpotRepository spotRepository;

    @Mock
    private SpotManagementValidationService spotManagementValidationService;

    @Mock
    private SpotMembershipService spotMembershipService;

    @Test
    void givenSpotManagementRequestWhenCreateSpotThenCreate() {
        //given
        final SpotManagementRequest spotManagementRequest = SpotManagementRequest.builder().build();
        final String companyUid = "companyUid";

        doNothing().when(spotManagementValidationService).validateSpotRequest(spotManagementRequest);
        final Company company = Company.builder().build();
        final Spot spot = SpotDataGenerator.spot(1, company);
        final Account account = AccountDataGenerator.companyAccount(1);
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);
        when(spotMappingService.toEntity(spotManagementRequest, accountAuthentication)).thenReturn(spot);
        when(spotRepository.save(spot)).thenReturn(spot);

        //when
        final ResourceOperationResponse response = spotManagementService.createSpot(spotManagementRequest, companyUid, accountAuthentication);

        //then
        assertThat(response.getOperationStatus()).isEqualTo(ResourceOperationStatus.CREATED);
    }

    @Test
    void givenExistingSpotAndSpotManagementRequestWhenUpdateSpotThenUpdate() {
        //given
        final SpotManagementRequest spotManagementRequest = SpotManagementRequest.builder().build();
        final String companyUid = "companyUid";

        doNothing().when(spotManagementValidationService).validateSpotRequest(spotManagementRequest);
        final Company company = Company.builder().build();
        final Spot spot = SpotDataGenerator.spot(1, company);
        final Long spotId = 1L;
        when(spotRepository.findByIdAndCompanyUid(spotId, companyUid)).thenReturn(Optional.of(spot));
        doNothing().when(spotMappingService).update(spot, spotManagementRequest);
        when(spotRepository.save(spot)).thenReturn(spot);

        //when
        final ResourceOperationResponse response = spotManagementService.updateSpot(spotManagementRequest, spotId, companyUid);

        //then
        assertThat(response.getOperationStatus()).isEqualTo(ResourceOperationStatus.UPDATED);
    }


    @Test
    void givenNotExistingSpotAndSpotManagementRequestWhenUpdateSpotThenError() {
        //given
        final SpotManagementRequest spotManagementRequest = SpotManagementRequest.builder().build();
        final String companyUid = "companyUid";

        final Long spotId = 1L;
        when(spotRepository.findByIdAndCompanyUid(spotId, companyUid)).thenReturn(Optional.empty());

        //when
        final Throwable throwable = catchThrowable(() -> spotManagementService.updateSpot(spotManagementRequest, spotId, companyUid));

        //then
        assertThat(throwable).isInstanceOf(RestEntityNotFoundException.class);
    }

    @Test
    void givenExistingSpotWhenDeleteSpotThenDelete() {
        //given
        final String companyUid = "companyUid";

        final Company company = Company.builder().build();
        final Spot spot = SpotDataGenerator.spot(1, company);
        final Long spotId = 1L;
        when(spotRepository.findByIdAndCompanyUid(spotId, companyUid)).thenReturn(Optional.of(spot));
        doNothing().when(spotRepository).delete(spot);
        doNothing().when(spotMembershipService).deleteMemberships(spot);

        //when
        final ResourceOperationResponse response = spotManagementService.deleteSpot(spotId, companyUid);

        //then
        assertThat(response.getOperationStatus()).isEqualTo(ResourceOperationStatus.DELETED);
    }


    @Test
    void givenNotExistingSpotWhenDeleteSpotThenError() {
        //given
        final String companyUid = "companyUid";

        final Long spotId = 1L;
        when(spotRepository.findByIdAndCompanyUid(spotId, companyUid)).thenReturn(Optional.empty());

        //when
        final Throwable throwable = catchThrowable(() -> spotManagementService.deleteSpot(spotId, companyUid));

        //then
        assertThat(throwable).isInstanceOf(RestEntityNotFoundException.class);
    }

    @Test
    void givenCompanyUidAndNameWhenFindCompanySpotsThenFind() {
        //given
        final String companyUid = "companyUid";
        final String name = "name";

        final Spot spot = Spot.builder().build();
        final SpotCompanyResponse response = SpotCompanyResponse.builder().build();
        when(spotRepository.findAllByCompanyUidAndNameStartsWith(companyUid, name)).thenReturn(List.of(spot));
        when(spotMappingService.toSpotCompanyResponse(spot)).thenReturn(response);
        //when
        final List<SpotCompanyResponse> result = spotManagementService.findCompanySpots(companyUid, name);

        //then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(response);
    }

    @Test
    void givenExistingSpotWhenFindMembershipsThenFind() {
        //given
        final Long spotId = 1L;
        final String companyUid = "companyUid";
        final String desiredCustomerSubstring = "desiredCustomerSubstring";
        final String membershipStatus = "membershipStatus";
        final Pageable pageable = Pageable.unpaged();

        final Spot spot = SpotDataGenerator.spot(1, null);
        final Page<SpotCompanyMembershipResponse> page = Page.empty();
        when(spotRepository.findByIdAndCompanyUid(spotId, companyUid)).thenReturn(Optional.of(spot));
        when(spotMembershipService.findMemberships(spot, desiredCustomerSubstring, membershipStatus, pageable))
                .thenReturn(page);

        //when
        final Page<SpotCompanyMembershipResponse> response = spotManagementService.findMemberships(spotId, companyUid, desiredCustomerSubstring, membershipStatus, pageable);

        //then
        assertThat(response).isEqualTo(page);
    }

    @Test
    void givenNotExistingSpotWhenFindMembershipsThenError() {
        //given
        final Long spotId = 1L;
        final String companyUid = "companyUid";
        final String desiredCustomerSubstring = "desiredCustomerSubstring";
        final String membershipStatus = "membershipStatus";
        final Pageable pageable = Pageable.unpaged();

        when(spotRepository.findByIdAndCompanyUid(spotId, companyUid)).thenReturn(Optional.empty());

        //when
        final Throwable throwable = catchThrowable(() -> spotManagementService.findMemberships(spotId, companyUid, desiredCustomerSubstring, membershipStatus, pageable));

        //then
        assertThat(throwable).isInstanceOf(RestEntityNotFoundException.class);
    }

    @Test
    void givenExistingSpotWhenUpdateMembershipsThenAccept() {
        //given
        final Long spotId = 1L;
        final String companyUid = "companyUid";
        final Long membershipId = 2L;
        final ResourceOperationResponse resourceOperationResponse = new ResourceOperationResponse(ResourceOperationStatus.UPDATED, 1L);
        final SpotCompanyMembershipManagementRequest spotCompanyMembershipManagementRequest = SpotCompanyMembershipManagementRequest.builder().build();

        final Spot spot = SpotDataGenerator.spot(1, null);
        when(spotRepository.findByIdAndCompanyUid(spotId, companyUid)).thenReturn(Optional.of(spot));
        when(spotMembershipService.updateMembership(spot, membershipId, spotCompanyMembershipManagementRequest)).thenReturn(resourceOperationResponse);

        //when
        final ResourceOperationResponse response = spotManagementService.updateMembership(spotId, companyUid, membershipId, spotCompanyMembershipManagementRequest);

        //then
        assertThat(response).isEqualTo(resourceOperationResponse);
    }

    @Test
    void givenNotExistingSpotWhenUpdateMembershipsThenError() {
        //given
        final Long spotId = 1L;
        final String companyUid = "companyUid";
        final Long membershipId = 2L;
        when(spotRepository.findByIdAndCompanyUid(spotId, companyUid)).thenReturn(Optional.empty());
        final SpotCompanyMembershipManagementRequest spotCompanyMembershipManagementRequest = SpotCompanyMembershipManagementRequest.builder().build();

        //when
        final Throwable throwable = catchThrowable(() -> spotManagementService.updateMembership(spotId, companyUid, membershipId, spotCompanyMembershipManagementRequest));

        //then
        assertThat(throwable).isInstanceOf(RestEntityNotFoundException.class);
    }
}