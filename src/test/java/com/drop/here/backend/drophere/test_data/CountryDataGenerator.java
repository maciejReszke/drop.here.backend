package com.drop.here.backend.drophere.test_data;

import com.drop.here.backend.drophere.country.Country;
import com.drop.here.backend.drophere.country.CountryStatus;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class CountryDataGenerator {
    public Country poland() {
        return Country.builder()
                .activatedAt(LocalDateTime.now())
                .countryStatus(CountryStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .mobilePrefix("+48")
                .name("Poland")
                .build();
    }
}
