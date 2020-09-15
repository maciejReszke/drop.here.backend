package com.drop.here.backend.drophere.product.service;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.common.rest.ResourceOperationStatus;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.product.dto.request.ProductCustomizationWrapperRequest;
import com.drop.here.backend.drophere.product.dto.request.ProductManagementRequest;
import com.drop.here.backend.drophere.product.dto.response.ProductResponse;
import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.product.entity.ProductCustomizationWrapper;
import com.drop.here.backend.drophere.product.entity.ProductUnit;
import com.drop.here.backend.drophere.product.repository.ProductRepository;
import com.drop.here.backend.drophere.schedule_template.service.ScheduleTemplateStoreService;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import com.drop.here.backend.drophere.test_data.AuthenticationDataGenerator;
import com.drop.here.backend.drophere.test_data.ProductDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductSearchingService productSearchingService;

    @Mock
    private ScheduleTemplateStoreService scheduleTemplateStoreService;
    @Mock
    private ProductValidationService productValidationService;

    @Mock
    private ProductMappingService productMappingService;

    @Mock
    private ProductCustomizationService productCustomizationService;

    @Test
    void givenRequestWhenFindAllThenFindAll() {
        //given
        final Pageable pageable = Pageable.unpaged();
        final String companyUid = "companyUid";
        final Account account = AccountDataGenerator.companyAccount(1);
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);
        final Page<ProductResponse> paged = Page.empty();

        final String[] desiredCategories = new String[0];
        final String desiredName = "aa";
        when(productSearchingService.findAll(pageable, companyUid, desiredCategories, desiredName, accountAuthentication)).thenReturn(paged);

        //when
        final Page<ProductResponse> result = productService.findAll(pageable, companyUid, desiredCategories, desiredName, accountAuthentication);

        //then
        assertThat(result).isEqualTo(paged);
    }

    @Test
    void givenProductManagementRequestWhenCreateProductThenCreate() {
        //given
        final ProductManagementRequest productManagementRequest = ProductManagementRequest.builder().build();
        final String companyUid = "companyUid";

        doNothing().when(productValidationService).validateProductRequest(productManagementRequest);
        final ProductUnit unit = ProductDataGenerator.unit(1);

        final Company company = Company.builder().build();
        final Product product = ProductDataGenerator.product(1, unit, company);
        final Account account = AccountDataGenerator.companyAccount(1);
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);
        when(productMappingService.toEntity(productManagementRequest, accountAuthentication)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);

        //when
        final ResourceOperationResponse response = productService.createProduct(productManagementRequest, companyUid, accountAuthentication);

        //then
        assertThat(response.getOperationStatus()).isEqualTo(ResourceOperationStatus.CREATED);
    }

    @Test
    void givenExistingProductAndProductManagementRequestWhenUpdateProductThenUpdate() {
        //given
        final ProductManagementRequest productManagementRequest = ProductManagementRequest.builder().build();
        final String companyUid = "companyUid";

        doNothing().when(productValidationService).validateProductRequest(productManagementRequest);
        final ProductUnit unit = ProductDataGenerator.unit(1);

        final Company company = Company.builder().build();
        final Product product = ProductDataGenerator.product(1, unit, company);
        final Long productId = 1L;
        when(productRepository.findByIdAndCompanyUid(productId, companyUid)).thenReturn(Optional.of(product));
        doNothing().when(productMappingService).update(product, productManagementRequest);
        when(productRepository.save(product)).thenReturn(product);

        //when
        final ResourceOperationResponse response = productService.updateProduct(productManagementRequest, productId, companyUid);

        //then
        assertThat(response.getOperationStatus()).isEqualTo(ResourceOperationStatus.UPDATED);
    }


    @Test
    void givenNotExistingProductAndProductManagementRequestWhenUpdateProductThenError() {
        //given
        final ProductManagementRequest productManagementRequest = ProductManagementRequest.builder().build();
        final String companyUid = "companyUid";

        final Long productId = 1L;
        when(productRepository.findByIdAndCompanyUid(productId, companyUid)).thenReturn(Optional.empty());

        //when
        final Throwable throwable = catchThrowable(() -> productService.updateProduct(productManagementRequest, productId, companyUid));

        //then
        assertThat(throwable).isInstanceOf(RestEntityNotFoundException.class);
    }

    @Test
    void givenExistingProductWhenDeleteProductThenDelete() {
        //given
        final String companyUid = "companyUid";

        final ProductUnit unit = ProductDataGenerator.unit(1);

        final Company company = Company.builder().build();
        final Product product = ProductDataGenerator.product(1, unit, company);
        final Long productId = 1L;
        when(productRepository.findByIdAndCompanyUid(productId, companyUid)).thenReturn(Optional.of(product));
        doNothing().when(productRepository).delete(product);
        doNothing().when(scheduleTemplateStoreService).deleteScheduleTemplateProductByProduct(product);

        //when
        final ResourceOperationResponse response = productService.deleteProduct(productId, companyUid);

        //then
        assertThat(response.getOperationStatus()).isEqualTo(ResourceOperationStatus.DELETED);
    }


    @Test
    void givenNotExistingProductWhenDeleteProductThenError() {
        //given
        final String companyUid = "companyUid";

        final Long productId = 1L;
        when(productRepository.findByIdAndCompanyUid(productId, companyUid)).thenReturn(Optional.empty());

        //when
        final Throwable throwable = catchThrowable(() -> productService.deleteProduct(productId, companyUid));

        //then
        assertThat(throwable).isInstanceOf(RestEntityNotFoundException.class);
    }

    @Test
    void givenExistingProductWhenCreateCustomizationThenCreate() {
        //given
        final Long productId = 1L;
        final String companyUid = "companyUid";
        final ProductCustomizationWrapperRequest request = ProductDataGenerator.productCustomizationWrapperRequest(1);

        final ProductUnit unit = ProductDataGenerator.unit(1);

        final Company company = Company.builder().build();
        final Product product = ProductDataGenerator.product(1, unit, company);
        final AccountAuthentication authentication = AccountAuthentication.builder().build();

        when(productRepository.findByIdAndCompanyUid(productId, companyUid)).thenReturn(Optional.of(product));
        when(productCustomizationService.createCustomizations(product, request, authentication)).thenReturn(ProductCustomizationWrapper.builder().build());

        //when
        final ResourceOperationResponse response = productService.createCustomization(productId, companyUid, request, authentication);

        //then
        assertThat(response.getOperationStatus()).isEqualTo(ResourceOperationStatus.CREATED);
    }

    @Test
    void givenNotExistingProductWhenCreateCustomizationThenError() {
        //given
        final Long productId = 1L;
        final String companyUid = "companyUid";
        final ProductCustomizationWrapperRequest request = ProductDataGenerator.productCustomizationWrapperRequest(1);
        final AccountAuthentication authentication = AccountAuthentication.builder().build();

        when(productRepository.findByIdAndCompanyUid(productId, companyUid)).thenReturn(Optional.empty());

        //when
        final Throwable throwable = catchThrowable(() -> productService.createCustomization(productId, companyUid, request, authentication));

        //then
        assertThat(throwable).isInstanceOf(RestEntityNotFoundException.class);
    }


    @Test
    void givenExistingProductWhenUpdateCustomizationThenUpdate() {
        //given
        final Long productId = 1L;
        final Long customizationId = 1L;
        final String companyUid = "companyUid";
        final ProductCustomizationWrapperRequest request = ProductDataGenerator.productCustomizationWrapperRequest(1);

        final ProductUnit unit = ProductDataGenerator.unit(1);

        final Company company = Company.builder().build();
        final Product product = ProductDataGenerator.product(1, unit, company);
        final AccountAuthentication authentication = AccountAuthentication.builder().build();

        when(productRepository.findByIdAndCompanyUid(productId, companyUid)).thenReturn(Optional.of(product));
        when(productCustomizationService.updateCustomization(product, customizationId, request, authentication)).thenReturn(ProductCustomizationWrapper.builder().build());

        //when
        final ResourceOperationResponse response = productService.updateCustomization(productId, companyUid, customizationId, request, authentication);

        //then
        assertThat(response.getOperationStatus()).isEqualTo(ResourceOperationStatus.UPDATED);
    }

    @Test
    void givenNotExistingProductWhenUpdateCustomizationThenError() {
        //given
        final Long productId = 1L;
        final String companyUid = "companyUid";
        final ProductCustomizationWrapperRequest request = ProductDataGenerator.productCustomizationWrapperRequest(1);
        final Long customizationId = 1L;
        final AccountAuthentication authentication = AccountAuthentication.builder().build();

        when(productRepository.findByIdAndCompanyUid(productId, companyUid)).thenReturn(Optional.empty());

        //when
        final Throwable throwable = catchThrowable(() -> productService.updateCustomization(productId, companyUid, customizationId, request, authentication));

        //then
        assertThat(throwable).isInstanceOf(RestEntityNotFoundException.class);
    }

    @Test
    void givenExistingProductWhenDeleteCustomizationThenDelete() {
        //given
        final Long productId = 1L;
        final Long customizationId = 1L;
        final String companyUid = "companyUid";

        final ProductUnit unit = ProductDataGenerator.unit(1);

        final Company company = Company.builder().build();
        final Product product = ProductDataGenerator.product(1, unit, company);
        final AccountAuthentication authentication = AccountAuthentication.builder().build();

        when(productRepository.findByIdAndCompanyUid(productId, companyUid)).thenReturn(Optional.of(product));
        doNothing().when(productCustomizationService).deleteCustomization(product, customizationId, authentication);

        //when
        final ResourceOperationResponse response = productService.deleteCustomization(productId, companyUid, customizationId, authentication);

        //then
        assertThat(response.getOperationStatus()).isEqualTo(ResourceOperationStatus.DELETED);
    }

    @Test
    void givenNotExistingProductWhenDeleteCustomizationThenError() {
        //given
        final Long productId = 1L;
        final String companyUid = "companyUid";
        final Long customizationId = 1L;
        final AccountAuthentication authentication = AccountAuthentication.builder().build();

        when(productRepository.findByIdAndCompanyUid(productId, companyUid)).thenReturn(Optional.empty());

        //when
        final Throwable throwable = catchThrowable(() -> productService.deleteCustomization(productId, companyUid, customizationId, authentication));

        //then
        assertThat(throwable).isInstanceOf(RestEntityNotFoundException.class);
    }

}