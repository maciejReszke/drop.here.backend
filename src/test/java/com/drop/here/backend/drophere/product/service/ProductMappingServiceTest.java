package com.drop.here.backend.drophere.product.service;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.product.dto.request.ProductManagementRequest;
import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.product.entity.ProductCustomizationWrapper;
import com.drop.here.backend.drophere.product.entity.ProductUnit;
import com.drop.here.backend.drophere.product.enums.ProductCreationType;
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
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductMappingServiceTest {

    @InjectMocks
    private ProductMappingService productMappingService;

    @Mock
    private ProductUnitService productUnitService;

    @Mock
    private ProductCustomizationMappingService productCustomizationMappingService;

    @Test
    void givenRequestWhenMapToEntityThenMap() {
        //given
        final ProductManagementRequest productManagementRequest = ProductDataGenerator.managementRequest(1);
        final Company company = Company.builder().build();
        final Account account = AccountDataGenerator.companyAccount(1);
        account.setCompany(company);
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);

        final ProductUnit productUnit = ProductUnit.builder().build();

        when(productUnitService.getByName(productManagementRequest.getUnit())).thenReturn(productUnit);
        when(productCustomizationMappingService.toCustomizationWrapper(any(), any())).thenReturn(ProductCustomizationWrapper.builder().build());

        //when
        final Product result = productMappingService.toEntity(productManagementRequest, accountAuthentication);

        //then
        assertThat(result.getCategory()).isEqualTo(productManagementRequest.getCategory());
        assertThat(result.getName()).isEqualTo(productManagementRequest.getName());
        assertThat(result.getPrice()).isEqualTo(productManagementRequest.getPrice().setScale(2, RoundingMode.DOWN));
        assertThat(result.getCreatedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusMinutes(1));
        assertThat(result.getLastUpdatedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusMinutes(1));
        assertThat(result.getUnitName()).isEqualTo(productUnit.getName());
        assertThat(result.getUnit()).isEqualTo(productUnit);
        assertThat(result.getCompany()).isEqualTo(company);
        assertThat(result.getDescription()).isEqualTo(productManagementRequest.getDescription());
        assertThat(result.getCreationType()).isEqualTo(ProductCreationType.PRODUCT);
        assertThat(result.getCustomizationWrappers()).hasSize(2);
    }

    @Test
    void givenProductWhenUpdateThenUpdate() {
        //given
        final ProductManagementRequest productManagementRequest = ProductDataGenerator.managementRequest(1);

        final ProductUnit productUnit = ProductUnit.builder().build();
        final ProductCustomizationWrapper wrapper = ProductCustomizationWrapper.builder().id(5L).build();
        final Product product = Product.builder().customizationWrappers(new ArrayList<>(List.of(wrapper))).build();
        wrapper.setProduct(product);

        when(productUnitService.getByName(productManagementRequest.getUnit())).thenReturn(productUnit);
        when(productCustomizationMappingService.toCustomizationWrapper(eq(product), any())).thenReturn(ProductCustomizationWrapper.builder().build());

        //when
        productMappingService.update(product, productManagementRequest);

        //then
        assertThat(product.getCategory()).isEqualTo(productManagementRequest.getCategory());
        assertThat(product.getName()).isEqualTo(productManagementRequest.getName());
        assertThat(product.getPrice()).isEqualTo(productManagementRequest.getPrice().setScale(2, RoundingMode.DOWN));
        assertThat(product.getCreatedAt()).isNull();
        assertThat(product.getLastUpdatedAt()).isBetween(LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusMinutes(1));
        assertThat(product.getUnitName()).isEqualTo(productUnit.getName());
        assertThat(product.getUnit()).isEqualTo(productUnit);
        assertThat(product.getCompany()).isNull();
        assertThat(product.getDescription()).isEqualTo(productManagementRequest.getDescription());
        assertThat(product.getCustomizationWrappers()).hasSize(2);
        assertThat(product.getCustomizationWrappers()).doesNotContain(wrapper);
        assertThat(wrapper.getProduct()).isNull();
    }
}