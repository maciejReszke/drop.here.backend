package com.drop.here.backend.drophere.spot.service;

import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.spot.entity.Spot;
import com.drop.here.backend.drophere.spot.repository.SpotRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
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
                .thenReturn(Optional.of(spot));

        //when
        final Spot result = spotPersistenceService.findSpot(spotUid, companyUid);

        //then
        assertThat(result).isEqualTo(spot);
    }

    @Test
    void givenNotExistingSpotWhenFindSpotThenException() {
        //given
        final String spotUid = "spotUid";
        final String companyUid = "companyUid";

        when(spotRepository.findByUidAndCompanyUid(spotUid, companyUid))
                .thenReturn(Optional.empty());

        //when
        final Throwable throwable = catchThrowable(() -> spotPersistenceService.findSpot(spotUid, companyUid));

        //then
        assertThat(throwable).isInstanceOf(RestEntityNotFoundException.class);
    }

    @Test
    void givenExistingSpotWhenFindSpotByIdAndCompanyThenFind() {
        //given
        final Long spotId = 1L;
        final Company company = Company.builder().build();
        final Spot spot = Spot.builder().build();

        when(spotRepository.findByIdAndCompany(spotId, company))
                .thenReturn(Optional.of(spot));

        //when
        final Spot result = spotPersistenceService.findSpot(spotId, company);

        //then
        assertThat(result).isEqualTo(spot);
    }

    @Test
    void givenNotExistingSpotWhenFindSpotByIdAndCompanyThenFind() {
        //given
        final Long spotId = 1L;
        final Company company = Company.builder().build();
        final Spot spot = Spot.builder().build();

        when(spotRepository.findByIdAndCompany(spotId, company))
                .thenReturn(Optional.empty());

        //when
        final Throwable throwable = catchThrowable(() -> spotPersistenceService.findSpot(spotId, company));

        //then
        assertThat(throwable).isInstanceOf(RestEntityNotFoundException.class);
    }
}