package com.drop.here.backend.drophere.product.service;

import com.drop.here.backend.drophere.common.exceptions.RestIllegalRequestValueException;
import com.drop.here.backend.drophere.product.dto.request.ProductManagementRequest;
import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.product.entity.ProductCategory;
import com.drop.here.backend.drophere.product.entity.ProductUnit;
import com.drop.here.backend.drophere.product.enums.ProductAvailabilityStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductValidationServiceTest {
    @InjectMocks
    private ProductValidationService productValidationService;

    @Mock
    private ProductCategoryService productCategoryService;

    @Mock
    private ProductUnitService productUnitService;

    @Test
    void givenValidRequestWhenValidateThenDoNothing() {
        //given
        final ProductManagementRequest productManagementRequest = ProductManagementRequest.builder()
                .availabilityStatus(ProductAvailabilityStatus.AVAILABLE.name())
                .category("category")
                .unit("unit")
                .build();

        when(productUnitService.getByName(productManagementRequest.getUnit())).thenReturn(ProductUnit.builder().build());
        when(productCategoryService.getByName(productManagementRequest.getCategory())).thenReturn(ProductCategory.builder().build());

        //when
        final Throwable throwable = catchThrowable(() -> productValidationService.validateProductRequest(productManagementRequest));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenInvalidAvailabilityStatusRequestWhenValidateThenThrowException() {
        //given
        final ProductManagementRequest productManagementRequest = ProductManagementRequest.builder()
                .availabilityStatus(ProductAvailabilityStatus.AVAILABLE.name() + "aa")
                .category("category")
                .unit("unit")
                .build();

        when(productUnitService.getByName(productManagementRequest.getUnit())).thenReturn(ProductUnit.builder().build());
        when(productCategoryService.getByName(productManagementRequest.getCategory())).thenReturn(ProductCategory.builder().build());

        //when
        final Throwable throwable = catchThrowable(() -> productValidationService.validateProductRequest(productManagementRequest));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
    }

    @Test
    void givenDeletableProductWhenValidateDeleteThenDoNothing() {
        //given
        final Product product = Product.builder().deletable(true).build();

        //when
        final Throwable throwable = catchThrowable(() -> productValidationService.validateProductDelete(product));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenNotDeletableProductWhenValidateDeleteThenThrowException() {
        //given
        final Product product = Product.builder().deletable(false).build();

        //when
        final Throwable throwable = catchThrowable(() -> productValidationService.validateProductDelete(product));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
    }


}