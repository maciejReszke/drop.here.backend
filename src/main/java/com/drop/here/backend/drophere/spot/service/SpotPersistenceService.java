package com.drop.here.backend.drophere.spot.service;

import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.spot.entity.Spot;
import com.drop.here.backend.drophere.spot.repository.SpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SpotPersistenceService {
    private final SpotRepository spotRepository;

    public Spot findSpot(String spotUid, String companyUid) {
        return spotRepository.findByUidAndCompanyUid(spotUid, companyUid)
                .orElseThrow(() -> new RestEntityNotFoundException(String.format(
                        "Spot with uid %s company %s was not found", spotUid, companyUid),
                        RestExceptionStatusCode.SPOT_NOT_FOUND_BY_UID));
    }

    public Spot findSpot(Long spotId, Company company) {
        return spotRepository.findByIdAndCompany(spotId, company)
                .orElseThrow(() -> new RestEntityNotFoundException(String.format(
                        "Spot with id %s company id %s was not found", spotId, company.getId()),
                        RestExceptionStatusCode.SPOT_NOT_FOUND_BY_ID));
    }

}
