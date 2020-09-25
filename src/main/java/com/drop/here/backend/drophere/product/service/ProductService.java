package com.drop.here.backend.drophere.product.service;

import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.common.rest.ResourceOperationStatus;
import com.drop.here.backend.drophere.configuration.security.AccountAuthentication;
import com.drop.here.backend.drophere.image.Image;
import com.drop.here.backend.drophere.image.ImageService;
import com.drop.here.backend.drophere.image.ImageType;
import com.drop.here.backend.drophere.product.dto.request.ProductCustomizationWrapperRequest;
import com.drop.here.backend.drophere.product.dto.request.ProductManagementRequest;
import com.drop.here.backend.drophere.product.dto.response.ProductResponse;
import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.product.repository.ProductRepository;
import com.drop.here.backend.drophere.schedule_template.service.ScheduleTemplateStoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

// TODO: 23/09/2020 zmienic ujsuwanie?
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductSearchingService productSearchingService;
    private final ProductValidationService productValidationService;
    private final ProductMappingService productMappingService;
    private final ProductCustomizationService productCustomizationService;
    private final ScheduleTemplateStoreService scheduleTemplateStoreService;
    private final ImageService imageService;

    public Flux<ProductResponse> findAll(Pageable pageable, String companyUid, String[] desiredCategories, String desiredNameSubstring, AccountAuthentication accountAuthentication) {
        return productSearchingService.findAll(pageable, companyUid, desiredCategories, desiredNameSubstring, accountAuthentication);
    }

    public Mono<ResourceOperationResponse> createProduct(ProductManagementRequest productManagementRequest, String companyUid, AccountAuthentication accountAuthentication) {
        return productValidationService.validateProductRequest(productManagementRequest)
                .flatMap(request -> productMappingService.toEntity(productManagementRequest, accountAuthentication))
                .doOnNext(product -> log.info("Creating product for company {} with name {}", companyUid, product.getName()))
                .flatMap(productRepository::save)
                .map(product -> new ResourceOperationResponse(ResourceOperationStatus.CREATED, product.getId()));
    }

    public Mono<ResourceOperationResponse> updateProduct(ProductManagementRequest productManagementRequest, String productId, String companyUid) {
        return getProduct(productId, companyUid)
                .flatMap(product -> productValidationService.validateProductRequest(productManagementRequest)
                        .flatMap(ignore -> productMappingService.update(product, productManagementRequest)))
                .doOnNext(product -> log.info("Updating product {} for company {} with name {}", product.getId(), companyUid, product.getName()))
                .flatMap(productRepository::save)
                .map(product -> new ResourceOperationResponse(ResourceOperationStatus.UPDATED, productId));
    }

    public Mono<Product> getProduct(String productId, String companyUid) {
        return productRepository.findByIdAndCompanyUid(productId, companyUid)
                .switchIfEmpty(Mono.error(() -> new RestEntityNotFoundException(String.format("Product with id %s company %s was not found", productId, companyUid), RestExceptionStatusCode.PRODUCT_NOT_FOUND)));
    }

    // TODO: 24/09/2020 transakcja!
    public Mono<ResourceOperationResponse> deleteProduct(String productId, String companyUid) {
        return getProduct(productId, companyUid)
                .doOnNext(product -> log.info("Deleting product {} for company {} with name {}", productId, companyUid, product.getName()))
                .flatMap(product -> scheduleTemplateStoreService.deleteScheduleTemplateProductByProduct(product)
                        .then(productRepository.delete(product)))
                .thenReturn(new ResourceOperationResponse(ResourceOperationStatus.DELETED, productId));
    }

    // TODO: 24/09/2020
    public Mono<ResourceOperationResponse> createCustomization(String productId, String companyUid, ProductCustomizationWrapperRequest productCustomizationWrapperRequest, AccountAuthentication authentication) {
        /*final Product product = getProduct(productId, companyUid);
        final ProductCustomizationWrapper productCustomizationWrapper = productCustomizationService.createCustomizations(product, productCustomizationWrapperRequest, authentication);
        return new ResourceOperationResponse(ResourceOperationStatus.CREATED, productCustomizationWrapper.getId());*/
        return Mono.empty();
    }

    // TODO: 24/09/2020
    public Mono<ResourceOperationResponse> updateCustomization(String productId, String companyUid, Long customizationId, ProductCustomizationWrapperRequest productCustomizationWrapperRequest, AccountAuthentication authentication) {
/*
        final Product product = getProduct(productId, companyUid);
        final ProductCustomizationWrapper productCustomizationWrapper = productCustomizationService.updateCustomization(product, customizationId, productCustomizationWrapperRequest, authentication);
        return new ResourceOperationResponse(ResourceOperationStatus.UPDATED, productCustomizationWrapper.getId());
*/
        return Mono.empty();
    }

    // TODO: 24/09/2020
    public Mono<ResourceOperationResponse> deleteCustomization(String productId, String companyUid, Long customizationId, AccountAuthentication authentication) {
/*
        final Product product = getProduct(productId, companyUid);
        productCustomizationService.deleteCustomization(product, customizationId, authentication);
        return new ResourceOperationResponse(ResourceOperationStatus.DELETED, customizationId);
*/
        return Mono.empty();
    }

    public Flux<String> findCategories(String companyUid) {
        return productRepository.findCategories(companyUid);
    }

    public Mono<ResourceOperationResponse> updateImage(String productId, String companyUid, FilePart imagePart, AccountAuthentication authentication) {
        return getProduct(productId, companyUid)
                .flatMap(product -> imageService.updateImage(imagePart, ImageType.PRODUCT_IMAGE, productId))
                .map(image -> new ResourceOperationResponse(ResourceOperationStatus.UPDATED, productId));
    }

    public Mono<Image> findImage(String productId, String companyUid) {
        return getProduct(productId, companyUid)
                .flatMap(product -> imageService.findImage(product.getId(), ImageType.PRODUCT_IMAGE));
    }
}
