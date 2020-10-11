package com.drop.here.backend.drophere.drop.service;

import com.drop.here.backend.drophere.drop.dto.DropCustomerSpotResponse;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.drop.repository.DropRepository;
import com.drop.here.backend.drophere.spot.entity.Spot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DropSearchingService {
    private final DropRepository dropRepository;
    public List<DropCustomerSpotResponse> findDrops(Spot spot, LocalDateTime from, LocalDateTime to) {
        return dropRepository.findBySpotAndStartTimeAfterAndStartTimeBefore(spot, from, to)
                .stream()
                .map(this::toDropCustomerSpotResponse)
                .collect(Collectors.toList());
    }

    private DropCustomerSpotResponse toDropCustomerSpotResponse(Drop drop) {
        return DropCustomerSpotResponse
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
