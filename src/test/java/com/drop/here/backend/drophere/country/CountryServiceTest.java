package com.drop.here.backend.drophere.country;

import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.test_data.CountryDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
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
        when(countryRepository.findAllByCountryStatus(CountryStatus.ACTIVE, Sort.by("name"))).thenReturn(List.of(Country
                .builder().name("poland").mobilePrefix("+48").build()));

        //when
        final List<CountryResponse> response = countryService.findAllActive();

        //then
        assertThat(response).hasSize(1);
        assertThat(response.get(0).getName()).isEqualTo("poland");
        assertThat(response.get(0).getMobilePrefix()).isEqualTo("+48");
    }

    @Test
    void givenExistingCountryWhenFindActiveThenFind() {
        //given
        final String countryName = "countryName";
        final Country country = CountryDataGenerator.poland();

        when(countryRepository.findByNameAndCountryStatus(countryName, CountryStatus.ACTIVE))
                .thenReturn(Optional.of(country));

        //when
        final Country active = countryService.findActive(countryName);

        //then
        assertThat(active).isEqualTo(country);
    }

    @Test
    void givenNotExistingCountryWhenFindActiveThenThrowException() {
        //given
        final String countryName = "countryName";

        when(countryRepository.findByNameAndCountryStatus(countryName, CountryStatus.ACTIVE))
                .thenReturn(Optional.empty());

        //when
        final Throwable throwable = catchThrowable(() -> countryService.findActive(countryName));

        //then
        assertThat(throwable).isInstanceOf(RestEntityNotFoundException.class);
    }

}