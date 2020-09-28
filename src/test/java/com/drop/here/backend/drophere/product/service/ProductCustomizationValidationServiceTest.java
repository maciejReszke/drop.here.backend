package com.drop.here.backend.drophere.product.service;

import com.drop.here.backend.drophere.common.exceptions.RestIllegalRequestValueException;
import com.drop.here.backend.drophere.product.dto.request.ProductCustomizationWrapperRequest;
import com.drop.here.backend.drophere.test_data.ProductDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(MockitoExtension.class)
class ProductCustomizationValidationServiceTest {

    @InjectMocks
    private ProductCustomizationValidationService validationService;

    @Test
    void givenValidRequestWhenValidateThenDoNothing() {
        //given
        final ProductCustomizationWrapperRequest request = ProductDataGenerator.productCustomizationWrapperRequest(1);

        //when
        final Throwable throwable = catchThrowable(() -> validationService.validate(List.of(request)));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenInvalidWrapperTypeWhenValidateThenError() {
        //given
        final ProductCustomizationWrapperRequest request = ProductDataGenerator.productCustomizationWrapperRequest(1);
        request.setType("okurwa");

        //when
        final Throwable throwable = catchThrowable(() -> validationService.validate(List.of(request)));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
    }

}