package com.drop.here.backend.drophere.spot.service;

import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.spot.entity.Spot;
import com.drop.here.backend.drophere.spot.repository.SpotRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpotPersistenceServiceTest {

    @InjectMocks
    private SpotPersistenceService spotPersistenceService;

    @Mock
    private SpotRepository spotRepository;

    @Test
    void givenExistingSpotWhenFindSpotThenFind() {
        //given
        final String spotUid = "spotUid";
        final String companyUid = "companyUid";
        final Spot spot = Spot.builder().build();

        when(spotRepository.findByUidAndCompanyUid(spotUid, companyUid))
                .thenReturn(Mono.just(spot));

        //when
        final Mono<Spot> result = spotPersistenceService.findSpot(spotUid, companyUid);

        //then
        StepVerifier.create(result)
                .expectNext(spot)
                .verifyComplete();
    }

    @Test
    void givenNotExistingSpotWhenFindSpotThenException() {
        //given
        final String spotUid = "spotUid";
        final String companyUid = "companyUid";

        when(spotRepository.findByUidAndCompanyUid(spotUid, companyUid))
                .thenReturn(Mono.empty());

        //when
        final Mono<Spot> result = spotPersistenceService.findSpot(spotUid, companyUid);

        //then
        StepVerifier.create(result)
                .expectError(RestEntityNotFoundException.class)
                .verify();
    }

}