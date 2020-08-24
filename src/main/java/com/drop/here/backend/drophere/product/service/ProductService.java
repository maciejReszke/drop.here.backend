package com.drop.here.backend.drophere.product.service;

import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.product.dto.ProductResponse;
import com.drop.here.backend.drophere.product.dto.request.ProductManagementRequest;
import com.drop.here.backend.drophere.product.repository.ProductRepository;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

// TODO: 24/08/2020
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductSearchingService productSearchingService;

    // TODO: 24/08/2020
    public Page<ProductResponse> findAll(Pageable pageable, String companyUid, AccountAuthentication accountAuthentication) {
        return productSearchingService.findAll(pageable, companyUid, accountAuthentication);
    }

    // TODO: 24/08/2020
    public ResourceOperationResponse createProduct(ProductManagementRequest productManagementRequest, String companyUid, AccountAuthentication accountAuthentication) {
        return null;
    }

    // TODO: 24/08/2020
    public ResourceOperationResponse updateProduct(ProductManagementRequest productManagementRequest, Long productId, String companyUid, AccountAuthentication accountAuthentication) {
        return null;
    }

    // TODO: 24/08/2020
    public ResourceOperationResponse deleteProduct(Long productId, String companyUid, AccountAuthentication accountAuthentication) {
        return null;
    }
}
