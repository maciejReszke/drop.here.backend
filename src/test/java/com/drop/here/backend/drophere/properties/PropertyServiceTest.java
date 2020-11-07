package com.drop.here.backend.drophere.properties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PropertyServiceTest {

    @InjectMocks
    private PropertyService propertyService;

    @Mock
    private PropertyRepository propertyRepository;

    @Test
    void givenExistingPropertyWhenGetPropertyThenGet() {
        //given
        final PropertyType propertyType = PropertyType.GOOGLE_CREDENTIALS_CONFIGURATION;
        final Property property = Property.builder().build();
        when(propertyRepository.findByPropertyType(propertyType)).thenReturn(Optional.of(property));

        //when
        final Property result = propertyService.getProperty(propertyType);

        //then
        assertThat(result).isEqualTo(property);
    }

    @Test
    void givenNotExistingPropertyWhenGetPropertyThenThrow() {
        //given
        final PropertyType propertyType = PropertyType.GOOGLE_CREDENTIALS_CONFIGURATION;
        when(propertyRepository.findByPropertyType(propertyType)).thenReturn(Optional.empty());

        //when
        final Throwable throwable = catchThrowable(() -> propertyService.getProperty(propertyType));

        //then
        assertThat(throwable).isInstanceOf(EntityNotFoundException.class);
    }

}