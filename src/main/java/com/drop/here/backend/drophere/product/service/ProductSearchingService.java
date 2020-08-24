package com.drop.here.backend.drophere.product.service;

import com.drop.here.backend.drophere.product.dto.ProductResponse;
import com.drop.here.backend.drophere.product.repository.ProductRepository;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductSearchingService {
    private final ProductRepository productRepository;

    // TODO: 24/08/2020
    public Page<ProductResponse> findAll(Pageable pageable, String companyUid, AccountAuthentication accountAuthentication) {
        return null;
    }
}
