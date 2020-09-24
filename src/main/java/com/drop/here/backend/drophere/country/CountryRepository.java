package com.drop.here.backend.drophere.country;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CountryRepository extends ReactiveMongoRepository<Country, String> {
    Flux<Country> findAllByCountryStatus(CountryStatus countryStatus, Sort sort);

    Mono<Country> findByNameAndCountryStatus(String country, CountryStatus status);
}
