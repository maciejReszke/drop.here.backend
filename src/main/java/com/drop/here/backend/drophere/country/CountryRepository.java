package com.drop.here.backend.drophere.country;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {
    List<Country> findAllByCountryStatus(CountryStatus countryStatus, Sort sort);

    Optional<Country> findByNameAndCountryStatus(String country, CountryStatus status);
}
