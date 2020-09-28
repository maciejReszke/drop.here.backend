package com.drop.here.backend.drophere.drop.service;

import com.drop.here.backend.drophere.drop.dto.DropRouteResponse;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.drop.repository.DropRepository;
import com.drop.here.backend.drophere.route.entity.Route;
import com.drop.here.backend.drophere.spot.dto.response.SpotCompanyResponse;
import com.drop.here.backend.drophere.spot.entity.Spot;
import com.drop.here.backend.drophere.spot.service.SpotMappingService;
import com.drop.here.backend.drophere.test_data.DropDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DropServiceTest {

    @InjectMocks
    private DropService dropService;

    @Mock
    private DropRepository dropRepository;

    @Mock
    private SpotMappingService spotMappingService;

    @Test
    void givenRouteWhenToDropResponsesThenMap() {
        //given
        final Route route = Route.builder().build();

        final Spot spot = Spot.builder().build();
        final Drop drop = DropDataGenerator.drop(1, null, spot);
        final SpotCompanyResponse spotResponse = SpotCompanyResponse.builder().build();
        when(dropRepository.findByRouteWithSpot(route)).thenReturn(List.of(drop));
        when(spotMappingService.toSpotCompanyResponse(spot)).thenReturn(spotResponse);

        //when
        final List<DropRouteResponse> response = dropService.toDropResponses(route);

        //then
        final DropRouteResponse dropResponse = response.get(0);
        assertThat(dropResponse.getDescription()).isEqualTo(drop.getDescription());
        assertThat(dropResponse.getEndTime()).isEqualTo(drop.getEndTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(dropResponse.getId()).isEqualTo(drop.getId());
        assertThat(dropResponse.getName()).isEqualTo(drop.getName());
        assertThat(dropResponse.getStartTime()).isEqualTo(drop.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(dropResponse.getSpotCompanyResponse()).isEqualTo(spotResponse);
        assertThat(dropResponse.getStatus()).isEqualTo(drop.getStatus());
        assertThat(dropResponse.getUid()).isEqualTo(drop.getUid());
    }

}