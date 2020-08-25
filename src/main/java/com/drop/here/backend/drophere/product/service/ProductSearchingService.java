package com.drop.here.backend.drophere.product.service;

import com.drop.here.backend.drophere.authentication.authentication.AuthenticationPrivilegesService;
import com.drop.here.backend.drophere.product.dto.response.ProductResponse;
import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.product.enums.ProductAvailabilityStatus;
import com.drop.here.backend.drophere.product.repository.ProductRepository;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductSearchingService {
    private final ProductRepository productRepository;
    private final AuthenticationPrivilegesService authenticationPrivilegesService;

    // TODO: 24/08/2020 company musi byc juz dostepne!!!
    public Page<ProductResponse> findAll(Pageable pageable,
                                         String companyUid,
                                         String[] desiredCategories,
                                         AccountAuthentication accountAuthentication) {
        final boolean isOwnCompanyOperation = authenticationPrivilegesService.isOwnCompanyOperation(accountAuthentication, companyUid);
        final ProductAvailabilityStatus[] desiredAvailabilityStatuses = getDesiredAvailabilityStatuses(isOwnCompanyOperation);
        return productRepository.findAll(companyUid, ArrayUtils.isEmpty(desiredCategories) ? null : desiredCategories, desiredAvailabilityStatuses, pageable)
                .map(product -> toProductResponse(product, isOwnCompanyOperation));
    }

    private ProductResponse toProductResponse(Product product, boolean isOwnCompanyOperation) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .category(product.getCategoryName())
                .unit(product.getUnitName())
                .availabilityStatus(product.getAvailabilityStatus())
                .price(product.getPrice())
                .description(product.getDescription())
                .deletable(isOwnCompanyOperation ? product.isDeletable() : null)
                .build();
    }

    private ProductAvailabilityStatus[] getDesiredAvailabilityStatuses(boolean isOwnCompanyOperation) {
        return isOwnCompanyOperation
                ? ProductAvailabilityStatus.values()
                : new ProductAvailabilityStatus[]{ProductAvailabilityStatus.AVAILABLE};
    }
}
