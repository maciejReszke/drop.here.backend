package com.drop.here.backend.drophere.country;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CountryService {
    private final CountryRepository countryRepository;
    private static final String SORT_BY_NAME_ATTRIBUTE = "name";

    public List<CountryResponse> findAllActive() {
        return countryRepository.findAllByCountryStatus(CountryStatus.ACTIVE, Sort.by(SORT_BY_NAME_ATTRIBUTE)).stream()
                .map(CountryResponse::from)
                .collect(Collectors.toList());
    }

    // TODO: 30/08/2020
    public Country findActive(String country) {
        return null;
    }
}
