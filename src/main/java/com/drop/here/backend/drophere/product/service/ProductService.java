package com.drop.here.backend.drophere.product.service;

import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.common.rest.ResourceOperationStatus;
import com.drop.here.backend.drophere.product.dto.request.ProductManagementRequest;
import com.drop.here.backend.drophere.product.dto.response.ProductResponse;
import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.product.repository.ProductRepository;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductSearchingService productSearchingService;
    private final ProductValidationService productValidationService;
    private final ProductMappingService productMappingService;

    public Page<ProductResponse> findAll(Pageable pageable, String companyUid, String[] desiredCategories, AccountAuthentication accountAuthentication) {
        return productSearchingService.findAll(pageable, companyUid, desiredCategories, accountAuthentication);
    }

    public ResourceOperationResponse createProduct(ProductManagementRequest productManagementRequest, String companyUid, AccountAuthentication accountAuthentication) {
        productValidationService.validate(productManagementRequest);
        final Product product = productMappingService.toEntity(productManagementRequest, accountAuthentication);
        log.info("Creating product for company {} with name {}", companyUid, product.getName());
        productRepository.save(product);
        return new ResourceOperationResponse(ResourceOperationStatus.CREATED, product.getId());
    }

    public ResourceOperationResponse updateProduct(ProductManagementRequest productManagementRequest, Long productId, String companyUid) {
        final Product product = getProduct(productId, companyUid);
        productValidationService.validate(productManagementRequest);
        productMappingService.update(product, productManagementRequest);
        log.info("Updating product {} for company {} with name {}", product.getId(), companyUid, product.getName());
        productRepository.save(product);
        return new ResourceOperationResponse(ResourceOperationStatus.UPDATED, productId);
    }

    private Product getProduct(Long productId, String companyUid) {
        return productRepository.findByIdAndCompanyUid(productId, companyUid)
                .orElseThrow(() -> new RestEntityNotFoundException(String.format("Product with id %s company %s was not found", productId, companyUid), RestExceptionStatusCode.PRODUCT_NOT_FOUND));
    }

    public ResourceOperationResponse deleteProduct(Long productId, String companyUid) {
        final Product product = getProduct(productId, companyUid);
        productValidationService.validateDelete(product);
        log.info("Deleting product {} for company {} with name {}", productId, companyUid, product.getName());
        productRepository.delete(product);
        return new ResourceOperationResponse(ResourceOperationStatus.DELETED, productId);
    }
}
