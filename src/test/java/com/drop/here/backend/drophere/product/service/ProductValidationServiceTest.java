package com.drop.here.backend.drophere.product.service;

import com.drop.here.backend.drophere.common.exceptions.RestIllegalRequestValueException;
import com.drop.here.backend.drophere.product.dto.request.ProductManagementRequest;
import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.product.entity.ProductUnit;
import com.drop.here.backend.drophere.product.enums.ProductCreationType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductValidationServiceTest {
    @InjectMocks
    private ProductValidationService productValidationService;

    @Mock
    private ProductUnitService productUnitService;

    @Mock
    private ProductCustomizationValidationService productCustomizationValidationService;

    @Test
    void givenValidRequestOnePieceWhenValidateThenDoNothing() {
        //given
        final ProductManagementRequest productManagementRequest = ProductManagementRequest.builder()
                .category("category")
                .unit("unit")
                .unitFraction(BigDecimal.ONE)
                .productCustomizationWrappers(List.of())
                .build();

        when(productUnitService.getByName(productManagementRequest.getUnit())).thenReturn(ProductUnit.builder().build());
        doNothing().when(productCustomizationValidationService).validate(any());

        //when
        final Throwable throwable = catchThrowable(() -> productValidationService.validateProductRequest(productManagementRequest));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenValidRequestMoreThanOnePieceWhenValidateThenDoNothing() {
        //given
        final ProductManagementRequest productManagementRequest = ProductManagementRequest.builder()
                .category("category")
                .unit("unit")
                .unitFraction(BigDecimal.valueOf(2))
                .productCustomizationWrappers(List.of())
                .build();

        when(productUnitService.getByName(productManagementRequest.getUnit())).thenReturn(ProductUnit.builder().build());
        doNothing().when(productCustomizationValidationService).validate(any());
        //when
        final Throwable throwable = catchThrowable(() -> productValidationService.validateProductRequest(productManagementRequest));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenValueFractionAndIsFractionableWhenValidateThenDoNothing() {
        //given
        final ProductManagementRequest productManagementRequest = ProductManagementRequest.builder()
                .category("category")
                .unit("unit")
                .unitFraction(BigDecimal.valueOf(0.1d))
                .productCustomizationWrappers(List.of())
                .build();

        final ProductUnit unit = ProductUnit.builder().fractionable(true).build();
        when(productUnitService.getByName(productManagementRequest.getUnit())).thenReturn(unit);
        doNothing().when(productCustomizationValidationService).validate(any());
        //when
        final Throwable throwable = catchThrowable(() -> productValidationService.validateProductRequest(productManagementRequest));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenNotFractionableButHasFractionValueWhenValidateThenDoNothing() {
        //given
        final ProductManagementRequest productManagementRequest = ProductManagementRequest.builder()
                .category("category")
                .unit("unit")
                .unitFraction(BigDecimal.valueOf(0.1d))
                .productCustomizationWrappers(List.of())
                .build();

        final ProductUnit unit = ProductUnit.builder().fractionable(false).build();
        when(productUnitService.getByName(productManagementRequest.getUnit())).thenReturn(unit);

        //when
        final Throwable throwable = catchThrowable(() -> productValidationService.validateProductRequest(productManagementRequest));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
    }

    @Test
    void givenValidRequestOnePieceWhenValidateUpdateThenDoNothing() {
        //given
        final ProductManagementRequest productManagementRequest = ProductManagementRequest.builder()
                .category("category")
                .unit("unit")
                .unitFraction(BigDecimal.ONE)
                .productCustomizationWrappers(List.of())
                .build();

        final Product product = Product.builder()
                .creationType(ProductCreationType.PRODUCT)
                .build();

        when(productUnitService.getByName(productManagementRequest.getUnit())).thenReturn(ProductUnit.builder().build());
        doNothing().when(productCustomizationValidationService).validate(any());
        //when
        final Throwable throwable = catchThrowable(() -> productValidationService.validateProductRequestUpdate(productManagementRequest, product));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenValidRequestInvalidCreationTypeWhenValidateUpdateThenThrowException() {
        //given
        final ProductManagementRequest productManagementRequest = ProductManagementRequest.builder()
                .category("category")
                .unit("unit")
                .unitFraction(BigDecimal.ONE)
                .productCustomizationWrappers(List.of())
                .build();

        final Product product = Product.builder()
                .creationType(ProductCreationType.ROUTE)
                .build();

        when(productUnitService.getByName(productManagementRequest.getUnit())).thenReturn(ProductUnit.builder().build());
        doNothing().when(productCustomizationValidationService).validate(any());
        //when
        final Throwable throwable = catchThrowable(() -> productValidationService.validateProductRequestUpdate(productManagementRequest, product));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
    }

    @Test
    void givenValidRequestMoreThanOnePieceWhenValidateUpdateThenDoNothing() {
        //given
        final ProductManagementRequest productManagementRequest = ProductManagementRequest.builder()
                .category("category")
                .unit("unit")
                .unitFraction(BigDecimal.valueOf(2))
                .productCustomizationWrappers(List.of())
                .build();

        final Product product = Product.builder()
                .creationType(ProductCreationType.PRODUCT)
                .build();

        when(productUnitService.getByName(productManagementRequest.getUnit())).thenReturn(ProductUnit.builder().build());
        doNothing().when(productCustomizationValidationService).validate(any());
        //when
        final Throwable throwable = catchThrowable(() -> productValidationService.validateProductRequestUpdate(productManagementRequest, product));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenValueFractionAndIsFractionableWhenValidateUpdateThenDoNothing() {
        //given
        final ProductManagementRequest productManagementRequest = ProductManagementRequest.builder()
                .category("category")
                .unit("unit")
                .unitFraction(BigDecimal.valueOf(0.1d))
                .productCustomizationWrappers(List.of())
                .build();

        final Product product = Product.builder()
                .creationType(ProductCreationType.PRODUCT)
                .build();

        final ProductUnit unit = ProductUnit.builder().fractionable(true).build();
        when(productUnitService.getByName(productManagementRequest.getUnit())).thenReturn(unit);
        doNothing().when(productCustomizationValidationService).validate(any());
        //when
        final Throwable throwable = catchThrowable(() -> productValidationService.validateProductRequestUpdate(productManagementRequest, product));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenNotFractionableButHasFractionValueWhenValidateUpdateThenDoNothing() {
        //given
        final ProductManagementRequest productManagementRequest = ProductManagementRequest.builder()
                .category("category")
                .unit("unit")
                .unitFraction(BigDecimal.valueOf(0.1d))
                .productCustomizationWrappers(List.of())
                .build();

        final Product product = Product.builder()
                .creationType(ProductCreationType.PRODUCT)
                .build();


        final ProductUnit unit = ProductUnit.builder().fractionable(false).build();
        when(productUnitService.getByName(productManagementRequest.getUnit())).thenReturn(unit);
        //when
        final Throwable throwable = catchThrowable(() -> productValidationService.validateProductRequestUpdate(productManagementRequest, product));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
    }

    @Test
    void givenValidProductWhenValidateModificationThenDoNothing() {
        //given
        final Product product = Product.builder()
                .creationType(ProductCreationType.PRODUCT)
                .customizationWrappers(List.of())
                .build();
        //when
        final Throwable throwable = catchThrowable(() -> productValidationService.validateProductModification(product));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenInvalidCreationTypeWhenValidateModificationThenDoNothing() {
        //given
        final Product product = Product.builder()
                .creationType(ProductCreationType.ROUTE)
                .customizationWrappers(List.of())
                .build();
        //when
        final Throwable throwable = catchThrowable(() -> productValidationService.validateProductModification(product));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
    }
}