package com.drop.here.backend.drophere.product.service;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.common.rest.ResourceOperationStatus;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.image.Image;
import com.drop.here.backend.drophere.image.ImageService;
import com.drop.here.backend.drophere.image.ImageType;
import com.drop.here.backend.drophere.product.dto.ProductCopy;
import com.drop.here.backend.drophere.product.dto.request.ProductManagementRequest;
import com.drop.here.backend.drophere.product.dto.response.ProductResponse;
import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.product.entity.ProductCustomizationWrapper;
import com.drop.here.backend.drophere.product.entity.ProductUnit;
import com.drop.here.backend.drophere.product.enums.ProductCreationType;
import com.drop.here.backend.drophere.product.repository.ProductRepository;
import com.drop.here.backend.drophere.route.repository.RouteProductRepository;
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
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    private ProductValidationService productValidationService;

    @Mock
    private ProductMappingService productMappingService;

    @Mock
    private ProductCustomizationService productCustomizationService;

    @Mock
    private ImageService imageService;

    @Mock
    private RouteProductRepository routeProductRepository;

    @Test
    void givenRequestWhenFindAllThenFindAll() {
        //given
        final Pageable pageable = Pageable.unpaged();
        final String companyUid = "companyUid";
        final Account account = AccountDataGenerator.companyAccount(1);
        final Page<ProductResponse> paged = Page.empty();

        final String[] desiredCategories = new String[0];
        final String desiredName = "aa";
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);
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

        final Company company = Company.builder().build();
        final ProductUnit unit = ProductDataGenerator.unit(1);
        final Product product = ProductDataGenerator.product(1, unit, company);
        doNothing().when(productValidationService).validateProductRequestUpdate(productManagementRequest, product);

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
        doNothing().when(routeProductRepository).nullOriginalProductId(productId);
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
    void givenImageWhenUpdateImageThenUpdate() throws IOException {
        //given
        final MockMultipartFile image = new MockMultipartFile("name", "byte".getBytes());
        final Product product = Product.builder().build();
        final Long productId = 1L;
        final String companyId = "companyId";
        final Image imageEntity = Image.builder().build();
        when(productRepository.findByIdAndCompanyUid(productId, companyId)).thenReturn(Optional.of(product));
        when(imageService.createImage(image.getBytes(), ImageType.PRODUCT_IMAGE))
                .thenReturn(imageEntity);
        when(productRepository.save(product)).thenReturn(product);

        //when
        final ResourceOperationResponse resourceOperationResponse = productService.updateImage(productId, companyId, image);

        //then
        assertThat(resourceOperationResponse.getOperationStatus()).isEqualTo(ResourceOperationStatus.UPDATED);
        assertThat(product.getImage()).isEqualTo(imageEntity);
    }

    @Test
    void givenExistingProductWithImageWhenFindImageThenFind() {
        //given
        final String companyId = "companyId";
        final Long productId = 5L;
        final Image image = Image.builder().build();
        final Product product = Product.builder()
                .image(image)
                .build();

        when(productRepository.findByIdAndCompanyUidWithImage(productId, companyId)).thenReturn(Optional.of(product));
        //when
        final Image result = productService.findImage(productId, companyId);

        //then
        assertThat(result).isEqualTo(image);
    }

    @Test
    void givenNotExistingCustomerWithImageWhenFindImageThenError() {
        //given
        final String companyId = "companyId";
        final Long productId = 5L;
        when(productRepository.findByIdAndCompanyUidWithImage(productId, companyId)).thenReturn(Optional.empty());
        //when
        final Throwable throwable = catchThrowable(() -> productService.findImage(productId, companyId));

        //then
        assertThat(throwable).isInstanceOf(RestEntityNotFoundException.class);
    }

    @Test
    void givenProductIdWhenCreateReadOnlyCopyThenCreate() {
        //given
        final Company company = Company.builder().build();
        final Product product = ProductDataGenerator.product(1, ProductUnit.builder().build(), company);
        product.setCustomizationWrappers(List.of(ProductCustomizationWrapper.builder().build()));
        product.setCreationType(ProductCreationType.PRODUCT);
        product.setId(5L);

        when(productCustomizationService.createReadOnlyCopies(eq(product), any())).thenReturn(List.of());
        when(productRepository.findByIdAndCompanyUid(product.getId(), company.getUid())).thenReturn(Optional.of(product));

        //when
        final ProductCopy copy = productService.createReadOnlyCopy(product.getId(), company, ProductCreationType.ROUTE);

        //then
        assertThat(product.getId()).isEqualTo(5L);
        assertThat(product.getCreationType()).isEqualTo(ProductCreationType.PRODUCT);
        assertThat(product.getCustomizationWrappers()).hasSize(1);
        assertThat(copy.getCopy().getId()).isNull();
        assertThat(copy.getCopy().getCreationType()).isEqualTo(ProductCreationType.ROUTE);
        assertThat(copy.getCopy().getCustomizationWrappers()).isEmpty();
        assertThat(copy.getOriginal().getId()).isEqualByComparingTo(5L);
        assertThat(copy.getOriginal().getCreationType()).isEqualTo(ProductCreationType.PRODUCT);
        assertThat(copy.getOriginal().getCustomizationWrappers()).hasSize(1);
    }

    @Test
    void givenNotExistingProductWhenCreateReadOnlyCopyThenThrowException() {
        //given
        final Company company = Company.builder().build();
        final Product product = ProductDataGenerator.product(1, ProductUnit.builder().build(), company);
        product.setCustomizationWrappers(List.of(ProductCustomizationWrapper.builder().build()));
        product.setCreationType(ProductCreationType.PRODUCT);
        product.setId(5L);

        when(productRepository.findByIdAndCompanyUid(product.getId(), company.getUid())).thenReturn(Optional.empty());

        //when
        final Throwable throwable = catchThrowable(() -> productService.createReadOnlyCopy(product.getId(), company, ProductCreationType.ROUTE));

        //then
        assertThat(throwable).isInstanceOf(RestEntityNotFoundException.class);
    }
}