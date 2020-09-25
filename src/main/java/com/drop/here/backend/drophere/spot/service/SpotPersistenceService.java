package com.drop.here.backend.drophere.spot.service;

import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.spot.entity.Spot;
import com.drop.here.backend.drophere.spot.repository.SpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SpotPersistenceService {
    private final SpotRepository spotRepository;

    public Mono<Spot> findSpot(String spotUid, String companyUid) {
        return spotRepository.findByUidAndCompanyUid(spotUid, companyUid)
                .switchIfEmpty(Mono.error(() -> new RestEntityNotFoundException(String.format(
                        "Spot with uid %s company %s was not found", spotUid, companyUid),
                        RestExceptionStatusCode.SPOT_NOT_FOUND_BY_UID)));
    }

}
