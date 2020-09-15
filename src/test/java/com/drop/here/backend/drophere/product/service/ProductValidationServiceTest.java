package com.drop.here.backend.drophere.product.service;

import com.drop.here.backend.drophere.common.exceptions.RestIllegalRequestValueException;
import com.drop.here.backend.drophere.product.dto.request.ProductManagementRequest;
import com.drop.here.backend.drophere.product.entity.ProductUnit;
import com.drop.here.backend.drophere.product.enums.ProductAvailabilityStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductValidationServiceTest {
    @InjectMocks
    private ProductValidationService productValidationService;

    @Mock
    private ProductUnitService productUnitService;

    @Test
    void givenValidRequestOnePieceWhenValidateThenDoNothing() {
        //given
        final ProductManagementRequest productManagementRequest = ProductManagementRequest.builder()
                .availabilityStatus(ProductAvailabilityStatus.AVAILABLE.name())
                .category("category")
                .unit("unit")
                .unitFraction(BigDecimal.ONE)
                .build();

        when(productUnitService.getByName(productManagementRequest.getUnit())).thenReturn(ProductUnit.builder().build());

        //when
        final Throwable throwable = catchThrowable(() -> productValidationService.validateProductRequest(productManagementRequest));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenValidRequestMoreThanOnePieceWhenValidateThenDoNothing() {
        //given
        final ProductManagementRequest productManagementRequest = ProductManagementRequest.builder()
                .availabilityStatus(ProductAvailabilityStatus.AVAILABLE.name())
                .category("category")
                .unit("unit")
                .unitFraction(BigDecimal.valueOf(2))
                .build();

        when(productUnitService.getByName(productManagementRequest.getUnit())).thenReturn(ProductUnit.builder().build());

        //when
        final Throwable throwable = catchThrowable(() -> productValidationService.validateProductRequest(productManagementRequest));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenValueFractionAndIsFractionableWhenValidateThenDoNothing() {
        //given
        final ProductManagementRequest productManagementRequest = ProductManagementRequest.builder()
                .availabilityStatus(ProductAvailabilityStatus.AVAILABLE.name())
                .category("category")
                .unit("unit")
                .unitFraction(BigDecimal.valueOf(0.1d))
                .build();

        final ProductUnit unit = ProductUnit.builder().fractionable(true).build();
        when(productUnitService.getByName(productManagementRequest.getUnit())).thenReturn(unit);
        //when
        final Throwable throwable = catchThrowable(() -> productValidationService.validateProductRequest(productManagementRequest));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenNotFractionableButHasFractionValueWhenValidateThenDoNothing() {
        //given
        final ProductManagementRequest productManagementRequest = ProductManagementRequest.builder()
                .availabilityStatus(ProductAvailabilityStatus.AVAILABLE.name())
                .category("category")
                .unit("unit")
                .unitFraction(BigDecimal.valueOf(0.1d))
                .build();

        final ProductUnit unit = ProductUnit.builder().fractionable(false).build();
        when(productUnitService.getByName(productManagementRequest.getUnit())).thenReturn(unit);

        //when
        final Throwable throwable = catchThrowable(() -> productValidationService.validateProductRequest(productManagementRequest));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
    }

    @Test
    void givenInvalidAvailabilityStatusRequestWhenValidateThenThrowException() {
        //given
        final ProductManagementRequest productManagementRequest = ProductManagementRequest.builder()
                .availabilityStatus(ProductAvailabilityStatus.AVAILABLE.name() + "aa")
                .category("category")
                .unit("unit")
                .unitFraction(BigDecimal.ONE)
                .build();

        when(productUnitService.getByName(productManagementRequest.getUnit())).thenReturn(ProductUnit.builder().build());

        //when
        final Throwable throwable = catchThrowable(() -> productValidationService.validateProductRequest(productManagementRequest));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
    }

}