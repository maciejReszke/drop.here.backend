package com.drop.here.backend.drophere.product.service;

import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.product.dto.request.ProductCustomizationWrapperRequest;
import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.product.entity.ProductCustomizationWrapper;
import com.drop.here.backend.drophere.product.entity.ProductUnit;
import com.drop.here.backend.drophere.product.repository.ProductCustomizationWrapperRepository;
import com.drop.here.backend.drophere.configuration.security.AccountAuthentication;
import com.drop.here.backend.drophere.test_data.ProductDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductCustomizationServiceTest {
    @InjectMocks
    private ProductCustomizationService productCustomizationService;

    @Mock
    private ProductCustomizationValidationService validationService;

    @Mock
    private ProductCustomizationMappingService mappingService;

    @Mock
    private ProductCustomizationWrapperRepository customizationWrapperRepository;

    @Test
    void givenRequestWhenCreateCustomizationsThenSave() {
        //given
        final ProductUnit unit = ProductDataGenerator.unit(1);
        final Company company = Company.builder().build();
        final Product product = ProductDataGenerator.product(1, unit, company);
        final ProductCustomizationWrapperRequest request = ProductDataGenerator.productCustomizationWrapperRequest(1);
        final ProductCustomizationWrapper wrapper = ProductCustomizationWrapper.builder().build();
        final AccountAuthentication authentication = AccountAuthentication.builder().company(company).build();

        doNothing().when(validationService).validate(request);
        when(mappingService.toCustomizationWrapper(product, request)).thenReturn(wrapper);
        when(customizationWrapperRepository.save(wrapper)).thenReturn(wrapper);

        //when

        final ProductCustomizationWrapper result = productCustomizationService.createCustomizations(product, request, authentication);

        //then
        assertThat(result).isEqualTo(wrapper);
    }

    @Test
    void givenExistingWrapperWhenUpdateThenUpdate() {
        //given
        final ProductUnit unit = ProductDataGenerator.unit(1);

        final Company company = Company.builder().build();
        final Product product = ProductDataGenerator.product(1, unit, company);
        final ProductCustomizationWrapperRequest request = ProductDataGenerator.productCustomizationWrapperRequest(1);
        final ProductCustomizationWrapper wrapper = ProductCustomizationWrapper.builder().build();
        final ProductCustomizationWrapper newWrapper = ProductCustomizationWrapper.builder().heading("h§").build();
        final Long customizationId = 1L;
        wrapper.setId(customizationId);
        final AccountAuthentication authentication = AccountAuthentication.builder().company(company).build();

        when(customizationWrapperRepository.findByIdAndProduct(customizationId, product)).thenReturn(Optional.of(wrapper));
        doNothing().when(validationService).validate(request);
        when(mappingService.toCustomizationWrapper(product, request)).thenReturn(newWrapper);
        when(customizationWrapperRepository.save(newWrapper)).thenReturn(newWrapper);

        //when
        final ProductCustomizationWrapper result = productCustomizationService.updateCustomization(product, customizationId, request, authentication);

        //then
        assertThat(result).isEqualTo(newWrapper);
        assertThat(newWrapper.getId()).isEqualTo(wrapper.getId());
    }

    @Test
    void givenNotExistingWrapperWhenUpdateThenError() {
        //given
        final ProductUnit unit = ProductDataGenerator.unit(1);

        final Company company = Company.builder().build();
        final Product product = ProductDataGenerator.product(1, unit, company);
        final ProductCustomizationWrapperRequest request = ProductDataGenerator.productCustomizationWrapperRequest(1);
        final ProductCustomizationWrapper wrapper = ProductCustomizationWrapper.builder().build();
        final Long customizationId = 1L;
        final AccountAuthentication authentication = AccountAuthentication.builder().company(company).build();
        wrapper.setId(customizationId);

        when(customizationWrapperRepository.findByIdAndProduct(customizationId, product)).thenReturn(Optional.empty());

        //when
        final Throwable throwable = catchThrowable(() -> productCustomizationService.updateCustomization(product, customizationId, request, authentication));

        //then
        assertThat(throwable).isInstanceOf(RestEntityNotFoundException.class);
    }

    @Test
    void givenExistingWrapperWhenDeleteThenDelete() {
        //given
        final ProductUnit unit = ProductDataGenerator.unit(1);

        final Company company = Company.builder().build();
        final Product product = ProductDataGenerator.product(1, unit, company);
        final ProductCustomizationWrapper wrapper = ProductCustomizationWrapper.builder().build();
        final Long customizationId = 1L;
        final AccountAuthentication authentication = AccountAuthentication.builder().company(company).build();

        when(customizationWrapperRepository.findByIdAndProduct(customizationId, product)).thenReturn(Optional.of(wrapper));
        doNothing().when(customizationWrapperRepository).delete(wrapper);

        //when
        productCustomizationService.deleteCustomization(product, customizationId, authentication);

        //then
        verifyNoMoreInteractions(customizationWrapperRepository);
    }

    @Test
    void givenNotExistingWrapperWhenDeleteThenError() {
        //given
        final ProductUnit unit = ProductDataGenerator.unit(1);

        final Company company = Company.builder().build();
        final Product product = ProductDataGenerator.product(1, unit, company);
        final ProductCustomizationWrapper wrapper = ProductCustomizationWrapper.builder().build();
        final Long customizationId = 1L;
        wrapper.setId(customizationId);
        final AccountAuthentication authentication = AccountAuthentication.builder().company(company).build();

        when(customizationWrapperRepository.findByIdAndProduct(customizationId, product)).thenReturn(Optional.empty());

        //when
        final Throwable throwable = catchThrowable(() -> productCustomizationService.deleteCustomization(product, customizationId, authentication));

        //then
        assertThat(throwable).isInstanceOf(RestEntityNotFoundException.class);
    }
}