package com.drop.here.backend.drophere.country;

import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.test_data.CountryDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CountryServiceTest {

    @InjectMocks
    private CountryService countryService;

    @Mock
    private CountryRepository countryRepository;

    @Test
    void whenFindAllActiveThenMapAndGet() {
        //given
        when(countryRepository.findAllByCountryStatus(CountryStatus.ACTIVE, Sort.by("name"))).thenReturn(Flux.just(Country
                .builder().name("poland").mobilePrefix("+48").build()));

        //when
        final Flux<CountryResponse> result = countryService.findAllActive();

        //then
        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response.getName()).isEqualTo("poland");
                    assertThat(response.getMobilePrefix()).isEqualTo("+48");
                })
                .verifyComplete();

    }

    @Test
    void givenExistingCountryWhenFindActiveThenFind() {
        //given
        final String countryName = "countryName";
        final Country country = CountryDataGenerator.poland();

        when(countryRepository.findByNameAndCountryStatus(countryName, CountryStatus.ACTIVE))
                .thenReturn(Mono.just(country));

        //when
        final Mono<Country> response = countryService.findActive(countryName);

        //then
        StepVerifier.create(response)
                .expectNext(country)
                .verifyComplete();
    }

    @Test
    void givenNotExistingCountryWhenFindActiveThenThrowException() {
        //given
        final String countryName = "countryName";

        when(countryRepository.findByNameAndCountryStatus(countryName, CountryStatus.ACTIVE))
                .thenReturn(Mono.empty());

        //when
        final Mono<Country> result = countryService.findActive(countryName);

        //then
        StepVerifier.create(result)
                .expectError(RestEntityNotFoundException.class)
                .verify();
    }

}