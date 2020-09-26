package com.drop.here.backend.drophere.drop.service;

import com.drop.here.backend.drophere.drop.dto.DropRouteResponse;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.drop.repository.DropRepository;
import com.drop.here.backend.drophere.route.entity.Route;
import com.drop.here.backend.drophere.spot.service.SpotMappingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DropService {
    private final DropRepository dropRepository;
    private final SpotMappingService spotMappingService;

    public List<DropRouteResponse> toDropResponses(Route route) {
        return dropRepository.findByRouteWithSpot(route)
                .stream()
                .map(this::toDropResponse)
                .collect(Collectors.toList());
    }

    private DropRouteResponse toDropResponse(Drop drop) {
        return DropRouteResponse
                .builder()
                .description(drop.getDescription())
                .endTime(drop.getEndTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .startTime(drop.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .name(drop.getName())
                .status(drop.getStatus())
                .uid(drop.getUid())
                .spotCompanyResponse(spotMappingService.toSpotCompanyResponse(drop.getSpot()))
                .build();
    }
}
