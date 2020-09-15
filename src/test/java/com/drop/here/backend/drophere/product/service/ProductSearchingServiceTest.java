package com.drop.here.backend.drophere.product.service;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.authentication.service.base.AuthenticationPrivilegesService;
import com.drop.here.backend.drophere.product.dto.response.ProductResponse;
import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.product.entity.ProductCustomizationWrapper;
import com.drop.here.backend.drophere.product.enums.ProductAvailabilityStatus;
import com.drop.here.backend.drophere.product.repository.ProductRepository;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import com.drop.here.backend.drophere.test_data.AuthenticationDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductSearchingServiceTest {

    @InjectMocks
    private ProductSearchingService productSearchingService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductCustomizationService productCustomizationService;

    @Mock
    private AuthenticationPrivilegesService authenticationPrivilegesService;

    @Test
    void givenOwnCompanyOperationWhenFindAllThenFind() {
        //given
        final Pageable pageable = Pageable.unpaged();
        final String companyUid = "companyUid";
        final String[] desiredCategories = new String[0];
        final Account account = AccountDataGenerator.customerAccount(1);
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);
        final Product product = Product.builder().id(1L).build();
        final String desiredName = "Name";

        when(authenticationPrivilegesService.isOwnCompanyOperation(accountAuthentication, companyUid))
                .thenReturn(true);
        when(productRepository.findAll(eq(companyUid), isNull(), eq('%' + desiredName.toLowerCase() + '%'), eq(ProductAvailabilityStatus.values()), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(product)));
        when(productCustomizationService.findCustomizations(any())).thenReturn(List.of(ProductCustomizationWrapper.builder().product(product).customizations(Set.of()).build()));

        //when
        final Page<ProductResponse> result = productSearchingService.findAll(pageable, companyUid, desiredCategories, desiredName, accountAuthentication);

        //then
        assertThat(result).hasSize(1);
    }

    @Test
    void givenNotOwnCompanyOperationWhenFindAllThenFind() {
        //given
        final Pageable pageable = Pageable.unpaged();
        final String companyUid = "companyUid";
        final String[] desiredCategories = new String[0];
        final Account account = AccountDataGenerator.customerAccount(1);
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);

        when(authenticationPrivilegesService.isOwnCompanyOperation(accountAuthentication, companyUid))
                .thenReturn(false);
        when(productRepository.findAll(eq(companyUid), isNull(), isNull(), eq(new ProductAvailabilityStatus[]{ProductAvailabilityStatus.AVAILABLE}), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(Product.builder().build())));

        //when
        final Page<ProductResponse> result = productSearchingService.findAll(pageable, companyUid, desiredCategories, "", accountAuthentication);

        //then
        assertThat(result).hasSize(1);
    }

}