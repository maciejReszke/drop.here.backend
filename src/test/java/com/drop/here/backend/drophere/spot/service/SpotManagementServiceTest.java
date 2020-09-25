package com.drop.here.backend.drophere.spot.service;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.common.rest.ResourceOperationStatus;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.configuration.security.AccountAuthentication;
import com.drop.here.backend.drophere.spot.dto.SpotCompanyMembershipManagementRequest;
import com.drop.here.backend.drophere.spot.dto.request.SpotManagementRequest;
import com.drop.here.backend.drophere.spot.dto.response.SpotCompanyMembershipResponse;
import com.drop.here.backend.drophere.spot.dto.response.SpotCompanyResponse;
import com.drop.here.backend.drophere.spot.entity.Spot;
import com.drop.here.backend.drophere.spot.repository.SpotRepository;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import com.drop.here.backend.drophere.test_data.AuthenticationDataGenerator;
import com.drop.here.backend.drophere.test_data.SpotDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
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
        when(spotRepository.save(spot)).thenReturn(Mono.just(spot));

        //when
        final Mono<ResourceOperationResponse> result = spotManagementService.createSpot(spotManagementRequest, companyUid, accountAuthentication);

        //then
        StepVerifier.create(result)
                .assertNext(response -> assertThat(response.getOperationStatus()).isEqualTo(ResourceOperationStatus.CREATED))
                .verifyComplete();
    }

    @Test
    void givenExistingSpotAndSpotManagementRequestWhenUpdateSpotThenUpdate() {
        //given
        final SpotManagementRequest spotManagementRequest = SpotManagementRequest.builder().build();
        final String companyUid = "companyUid";

        doNothing().when(spotManagementValidationService).validateSpotRequest(spotManagementRequest);
        final Company company = Company.builder().build();
        final Spot spot = SpotDataGenerator.spot(1, company);
        final String spotId = "1L";
        when(spotRepository.findByIdAndCompanyUid(spotId, companyUid)).thenReturn(Mono.just(spot));
        doNothing().when(spotMappingService).update(spot, spotManagementRequest);
        when(spotRepository.save(spot)).thenReturn(Mono.just(spot));

        //when
        final Mono<ResourceOperationResponse> result = spotManagementService.updateSpot(spotManagementRequest, spotId, companyUid);

        //then
        StepVerifier.create(result)
                .assertNext(response -> assertThat(response.getOperationStatus()).isEqualTo(ResourceOperationStatus.UPDATED))
                .verifyComplete();
    }


    @Test
    void givenNotExistingSpotAndSpotManagementRequestWhenUpdateSpotThenError() {
        //given
        final SpotManagementRequest spotManagementRequest = SpotManagementRequest.builder().build();
        final String companyUid = "companyUid";

        final String spotId = "1L";
        when(spotRepository.findByIdAndCompanyUid(spotId, companyUid)).thenReturn(Mono.empty());

        //when
        final Mono<ResourceOperationResponse> result = spotManagementService.updateSpot(spotManagementRequest, spotId, companyUid);

        //then
        StepVerifier.create(result)
                .expectError(RestEntityNotFoundException.class)
                .verify();
    }

    @Test
    void givenExistingSpotWhenDeleteSpotThenDelete() {
        //given
        final String companyUid = "companyUid";

        final Company company = Company.builder().build();
        final Spot spot = SpotDataGenerator.spot(1, company);
        final String spotId = "1L";
        when(spotRepository.findByIdAndCompanyUid(spotId, companyUid)).thenReturn(Mono.just(spot));
        when(spotRepository.delete(spot)).thenReturn(Mono.empty());
        when(spotMembershipService.deleteMemberships(spot)).thenReturn(Mono.empty());

        //when
        final Mono<ResourceOperationResponse> result = spotManagementService.deleteSpot(spotId, companyUid);

        //then
        StepVerifier.create(result)
                .assertNext(response -> assertThat(response.getOperationStatus()).isEqualTo(ResourceOperationStatus.DELETED))
                .verifyComplete();
    }


    @Test
    void givenNotExistingSpotWhenDeleteSpotThenError() {
        //given
        final String companyUid = "companyUid";

        final String spotId = "1L";
        when(spotRepository.findByIdAndCompanyUid(spotId, companyUid)).thenReturn(Mono.empty());

        //when
        final Mono<ResourceOperationResponse> result = spotManagementService.deleteSpot(spotId, companyUid);

        //then
        StepVerifier.create(result)
                .expectError(RestEntityNotFoundException.class)
                .verify();
    }

    @Test
    void givenCompanyUidAndNameWhenFindCompanySpotsThenFind() {
        //given
        final String companyUid = "companyUid";
        final String name = "name";

        final Spot spot = Spot.builder().build();
        final SpotCompanyResponse response = SpotCompanyResponse.builder().build();
        when(spotRepository.findAllByCompanyUidAndNameStartsWith(companyUid, name)).thenReturn(Flux.just(spot));
        when(spotMappingService.toSpotCompanyResponse(spot)).thenReturn(response);
        //when
        final Flux<SpotCompanyResponse> result = spotManagementService.findCompanySpots(companyUid, name);

        //then
        StepVerifier.create(result)
                .expectNext(response)
                .verifyComplete();
    }

    @Test
    void givenExistingSpotWhenFindMembershipsThenFind() {
        //given
        final String spotId = "1L";
        final String companyUid = "companyUid";
        final String desiredCustomerSubstring = "desiredCustomerSubstring";
        final String membershipStatus = "membershipStatus";
        final Pageable pageable = Pageable.unpaged();

        final Spot spot = SpotDataGenerator.spot(1, null);
        when(spotRepository.findByIdAndCompanyUid(spotId, companyUid)).thenReturn(Mono.just(spot));
        when(spotMembershipService.findMemberships(spot, desiredCustomerSubstring, membershipStatus, pageable))
                .thenReturn(Flux.empty());

        //when
        final Flux<SpotCompanyMembershipResponse> result = spotManagementService.findMemberships(spotId, companyUid, desiredCustomerSubstring, membershipStatus, pageable);

        //then
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void givenNotExistingSpotWhenFindMembershipsThenError() {
        //given
        final String spotId = "1L";
        final String companyUid = "companyUid";
        final String desiredCustomerSubstring = "desiredCustomerSubstring";
        final String membershipStatus = "membershipStatus";
        final Pageable pageable = Pageable.unpaged();

        when(spotRepository.findByIdAndCompanyUid(spotId, companyUid)).thenReturn(Mono.empty());

        //when
        final Flux<SpotCompanyMembershipResponse> result = spotManagementService.findMemberships(spotId, companyUid, desiredCustomerSubstring, membershipStatus, pageable);

        //then
        StepVerifier.create(result)
                .expectError(RestEntityNotFoundException.class)
                .verify();
    }

    @Test
    void givenExistingSpotWhenUpdateMembershipsThenAccept() {
        //given
        final String spotId = "1L";
        final String companyUid = "companyUid";
        final Long membershipId = 2L;
        final ResourceOperationResponse resourceOperationResponse = new ResourceOperationResponse(ResourceOperationStatus.UPDATED, "1L");
        final SpotCompanyMembershipManagementRequest spotCompanyMembershipManagementRequest = SpotCompanyMembershipManagementRequest.builder().build();

        final Spot spot = SpotDataGenerator.spot(1, null);
        when(spotRepository.findByIdAndCompanyUid(spotId, companyUid)).thenReturn(Mono.just(spot));
        when(spotMembershipService.updateMembership(spot, membershipId, spotCompanyMembershipManagementRequest))
                .thenReturn(Mono.just(resourceOperationResponse));

        //when
        final Mono<ResourceOperationResponse> result = spotManagementService.updateMembership(spotId, companyUid, membershipId, spotCompanyMembershipManagementRequest);

        //then
        StepVerifier.create(result)
                .expectNext(resourceOperationResponse)
                .verifyComplete();
    }

    @Test
    void givenNotExistingSpotWhenUpdateMembershipsThenError() {
        //given
        final String spotId = "1L";
        final String companyUid = "companyUid";
        final Long membershipId = 2L;
        when(spotRepository.findByIdAndCompanyUid(spotId, companyUid)).thenReturn(Mono.empty());
        final SpotCompanyMembershipManagementRequest spotCompanyMembershipManagementRequest = SpotCompanyMembershipManagementRequest.builder().build();

        //when
        final Mono<ResourceOperationResponse> result = spotManagementService.updateMembership(spotId, companyUid, membershipId, spotCompanyMembershipManagementRequest);

        //then
        StepVerifier.create(result)
                .expectError(RestEntityNotFoundException.class)
                .verify();
    }
}