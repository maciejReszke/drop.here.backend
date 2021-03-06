package com.drop.here.backend.drophere.product.service;

import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.product.dto.response.ProductUnitResponse;
import com.drop.here.backend.drophere.product.entity.ProductUnit;
import com.drop.here.backend.drophere.product.repository.ProductUnitRepository;
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
class ProductUnitServiceTest {
    @InjectMocks
    private ProductUnitService productUnitService;

    @Mock
    private ProductUnitRepository productUnitRepository;

    @Test
    void givenExistingProductUnitWhenGetByNameThenGet() {
        //given
        final String name = "name";
        final ProductUnit unit = ProductUnit.builder().build();
        when(productUnitRepository.findByName(name)).thenReturn(Optional.of(unit));

        //when
        final ProductUnit result = productUnitService.getByName(name);

        //then
        assertThat(result).isEqualTo(unit);
    }

    @Test
    void givenNotExistingProductUnitWhenGetByNameThenException() {
        //given
        final String name = "name";
        when(productUnitRepository.findByName(name)).thenReturn(Optional.empty());

        //when
        final Throwable throwable = catchThrowable(() -> productUnitService.getByName(name));

        //then
        assertThat(throwable).isInstanceOf(RestEntityNotFoundException.class);
    }

    @Test
    void whenFindAllThenMapAndGet() {
        //given
        when(productUnitRepository.findAll(Sort.by("name"))).thenReturn(List.of(ProductUnit
                .builder().name("unit").build()));

        //when
        final List<ProductUnitResponse> response = productUnitService.findAll();

        //then
        assertThat(response).hasSize(1);
        assertThat(response.get(0).getName()).isEqualTo("unit");
    }
}