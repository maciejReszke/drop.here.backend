package com.drop.here.backend.drophere.product.service;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.company.Company;
import com.drop.here.backend.drophere.product.dto.request.ProductManagementRequest;
import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.product.entity.ProductCategory;
import com.drop.here.backend.drophere.product.entity.ProductUnit;
import com.drop.here.backend.drophere.product.enums.ProductAvailabilityStatus;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import com.drop.here.backend.drophere.test_data.AuthenticationDataGenerator;
import com.drop.here.backend.drophere.test_data.ProductDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.RoundingMode;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductMappingServiceTest {

    @InjectMocks
    private ProductMappingService productMappingService;

    @Mock
    private ProductCategoryService categoryService;

    @Mock
    private ProductUnitService productUnitService;

    @Test
    void givenRequestWhenMapToEntityThenMap() {
        //given
        final ProductManagementRequest productManagementRequest = ProductDataGenerator.managementRequest(1);
        final Company company = Company.builder().build();
        final Account account = AccountDataGenerator.companyAccount(1);
        account.setCompany(company);
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);
        final ProductCategory productCategory = ProductCategory.builder().build();
        final ProductUnit productUnit = ProductUnit.builder().build();

        when(categoryService.getByName(productManagementRequest.getCategory())).thenReturn(productCategory);
        when(productUnitService.getByName(productManagementRequest.getUnit())).thenReturn(productUnit);

        //when
        final Product result = productMappingService.toEntity(productManagementRequest, accountAuthentication);

        //then
        assertThat(result.getCategory()).isEqualTo(productCategory);
        assertThat(result.getName()).isEqualTo(productManagementRequest.getName());
        assertThat(result.getPrice()).isEqualTo(productManagementRequest.getPrice().setScale(2, RoundingMode.DOWN));
        assertThat(result.getAvailabilityStatus()).isEqualTo(ProductAvailabilityStatus.UNAVAILABLE);
        assertThat(result.getCreatedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusMinutes(1));
        assertThat(result.getLastUpdatedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusMinutes(1));
        assertThat(result.getCategoryName()).isEqualTo(productCategory.getName());
        assertThat(result.getUnitName()).isEqualTo(productUnit.getName());
        assertThat(result.getUnit()).isEqualTo(productUnit);
        assertThat(result.isDeletable()).isTrue();
        assertThat(result.getCompany()).isEqualTo(company);
        assertThat(result.getDescription()).isEqualTo(productManagementRequest.getDescription());
    }

    @Test
    void givenProductWhenUpdateThenUpdate() {
        //given
        final ProductManagementRequest productManagementRequest = ProductDataGenerator.managementRequest(1);
        final ProductCategory productCategory = ProductCategory.builder().build();
        final ProductUnit productUnit = ProductUnit.builder().build();
        final Product product = Product.builder().build();

        when(categoryService.getByName(productManagementRequest.getCategory())).thenReturn(productCategory);
        when(productUnitService.getByName(productManagementRequest.getUnit())).thenReturn(productUnit);

        //when
        productMappingService.update(product, productManagementRequest);

        //then
        assertThat(product.getCategory()).isEqualTo(productCategory);
        assertThat(product.getName()).isEqualTo(productManagementRequest.getName());
        assertThat(product.getPrice()).isEqualTo(productManagementRequest.getPrice().setScale(2, RoundingMode.DOWN));
        assertThat(product.getAvailabilityStatus()).isEqualTo(ProductAvailabilityStatus.UNAVAILABLE);
        assertThat(product.getCreatedAt()).isNull();
        assertThat(product.getLastUpdatedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusMinutes(1));
        assertThat(product.getCategoryName()).isEqualTo(productCategory.getName());
        assertThat(product.getUnitName()).isEqualTo(productUnit.getName());
        assertThat(product.getUnit()).isEqualTo(productUnit);
        assertThat(product.getCompany()).isNull();
        assertThat(product.isDeletable()).isFalse();
        assertThat(product.getDescription()).isEqualTo(productManagementRequest.getDescription());
    }
}