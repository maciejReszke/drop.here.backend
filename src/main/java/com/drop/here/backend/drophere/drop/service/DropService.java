package com.drop.here.backend.drophere.drop.service;

import com.drop.here.backend.drophere.drop.dto.DropCustomerShortResponse;
import com.drop.here.backend.drophere.drop.dto.DropRouteResponse;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.drop.repository.DropRepository;
import com.drop.here.backend.drophere.route.entity.Route;
import com.drop.here.backend.drophere.spot.entity.Spot;
import com.drop.here.backend.drophere.spot.service.SpotMappingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
                .map(this::toDropRouteResponse)
                .collect(Collectors.toList());
    }

    private DropRouteResponse toDropRouteResponse(Drop drop) {
        return DropRouteResponse
                .builder()
                .description(drop.getDescription())
                .endTime(drop.getEndTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .startTime(drop.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .name(drop.getName())
                .status(drop.getStatus())
                .uid(drop.getUid())
                .id(drop.getId())
                .spotCompanyResponse(spotMappingService.toSpotCompanyResponse(drop.getSpot()))
                .build();
    }

    public List<DropCustomerShortResponse> findDrops(Spot spot, LocalDateTime from, LocalDateTime to) {
        return dropRepository.findBySpotAndStartTimeAfterAndStartTimeBefore(spot, from, to)
                .stream()
                .map(this::toDropCustomerShortResponse)
                .collect(Collectors.toList());
    }

    private DropCustomerShortResponse toDropCustomerShortResponse(Drop drop) {
        return DropCustomerShortResponse
                .builder()
                .description(drop.getDescription())
                .endTime(drop.getEndTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .startTime(drop.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .name(drop.getName())
                .status(drop.getStatus())
                .uid(drop.getUid())
                .build();
    }
}
