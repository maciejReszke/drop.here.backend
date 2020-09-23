package com.drop.here.backend.drophere.product.service;

import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.common.exceptions.RestIllegalRequestValueException;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.common.rest.ResourceOperationStatus;
import com.drop.here.backend.drophere.image.Image;
import com.drop.here.backend.drophere.image.ImageService;
import com.drop.here.backend.drophere.image.ImageType;
import com.drop.here.backend.drophere.product.dto.request.ProductCustomizationWrapperRequest;
import com.drop.here.backend.drophere.product.dto.request.ProductManagementRequest;
import com.drop.here.backend.drophere.product.dto.response.ProductResponse;
import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.product.entity.ProductCustomizationWrapper;
import com.drop.here.backend.drophere.product.repository.ProductRepository;
import com.drop.here.backend.drophere.schedule_template.service.ScheduleTemplateStoreService;
import com.drop.here.backend.drophere.configuration.security.AccountAuthentication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

// TODO MONO:
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

    // TODO: 23/09/2020 zmienic ujsuwanie?
    public Flux<ProductResponse> findAll(Pageable pageable, String companyUid, String[] desiredCategories, String desiredNameSubstring, AccountAuthentication accountAuthentication) {
        return productSearchingService.findAll(pageable, companyUid, desiredCategories, desiredNameSubstring, accountAuthentication);
    }

    public Mono<ResourceOperationResponse> createProduct(ProductManagementRequest productManagementRequest, String companyUid, AccountAuthentication accountAuthentication) {
        productValidationService.validateProductRequest(productManagementRequest);
        final Product product = productMappingService.toEntity(productManagementRequest, accountAuthentication);
        log.info("Creating product for company {} with name {}", companyUid, product.getName());
        productRepository.save(product);
        return new ResourceOperationResponse(ResourceOperationStatus.CREATED, product.getId());
    }

    public Mono<ResourceOperationResponse> updateProduct(ProductManagementRequest productManagementRequest, Long productId, String companyUid) {
        final Product product = getProduct(productId, companyUid);
        productValidationService.validateProductRequest(productManagementRequest);
        productMappingService.update(product, productManagementRequest);
        log.info("Updating product {} for company {} with name {}", product.getId(), companyUid, product.getName());
        productRepository.save(product);
        return new ResourceOperationResponse(ResourceOperationStatus.UPDATED, productId);
    }

    public Product getProduct(Long productId, String companyUid) {
        return productRepository.findByIdAndCompanyUid(productId, companyUid)
                .orElseThrow(() -> new RestEntityNotFoundException(String.format("Product with id %s company %s was not found", productId, companyUid), RestExceptionStatusCode.PRODUCT_NOT_FOUND));
    }

    // todo bylo transactional(rollbackFor = Exception.class)
    public Mono<ResourceOperationResponse> deleteProduct(Long productId, String companyUid) {
        final Product product = getProduct(productId, companyUid);
        log.info("Deleting product {} for company {} with name {}", productId, companyUid, product.getName());
        scheduleTemplateStoreService.deleteScheduleTemplateProductByProduct(product);
        productRepository.delete(product);
        return new ResourceOperationResponse(ResourceOperationStatus.DELETED, productId);
    }

    public Mono<ResourceOperationResponse> createCustomization(Long productId, String companyUid, ProductCustomizationWrapperRequest productCustomizationWrapperRequest, AccountAuthentication authentication) {
        final Product product = getProduct(productId, companyUid);
        final ProductCustomizationWrapper productCustomizationWrapper = productCustomizationService.createCustomizations(product, productCustomizationWrapperRequest, authentication);
        return new ResourceOperationResponse(ResourceOperationStatus.CREATED, productCustomizationWrapper.getId());
    }

    public Mono<ResourceOperationResponse> updateCustomization(Long productId, String companyUid, Long customizationId, ProductCustomizationWrapperRequest productCustomizationWrapperRequest, AccountAuthentication authentication) {
        final Product product = getProduct(productId, companyUid);
        final ProductCustomizationWrapper productCustomizationWrapper = productCustomizationService.updateCustomization(product, customizationId, productCustomizationWrapperRequest, authentication);
        return new ResourceOperationResponse(ResourceOperationStatus.UPDATED, productCustomizationWrapper.getId());
    }

    public Mono<ResourceOperationResponse> deleteCustomization(Long productId, String companyUid, Long customizationId, AccountAuthentication authentication) {
        final Product product = getProduct(productId, companyUid);
        productCustomizationService.deleteCustomization(product, customizationId, authentication);
        return new ResourceOperationResponse(ResourceOperationStatus.DELETED, customizationId);
    }

    public List<String> findCategories(String companyUid) {
        return productRepository.findCategories(companyUid);
    }

    public Mono<ResourceOperationResponse> updateImage(Long productId, String companyUid, MultipartFile imagePart, AccountAuthentication authentication) {
        try {
            final Product product = getProduct(productId, companyUid);
            final Image image = imageService.createImage(imagePart.getBytes(), ImageType.PRODUCT_IMAGE);
            product.setImage(image);
            log.info("Updating image for product {} company {}", productId, companyUid);
            productRepository.save(product);
            return new ResourceOperationResponse(ResourceOperationStatus.UPDATED, product.getId());
        } catch (IOException exception) {
            throw new RestIllegalRequestValueException("Invalid image " + exception.getMessage(),
                    RestExceptionStatusCode.UPDATE_PRODUCT_IMAGE_INVALID_IMAGE);
        }
    }

    // todo bylo transactional(readOnly = true)
    public Mono<Image> findImage(Long productId, String companyUid) {
        return productRepository.findByIdAndCompanyUidWithImage(productId, companyUid)
                .orElseThrow(() -> new RestEntityNotFoundException(String.format(
                        "Image for product %s company %s was not found", productId, companyUid),
                        RestExceptionStatusCode.PRODUCT_IMAGE_WAS_NOT_FOUND))
                .getImage();
    }
}
