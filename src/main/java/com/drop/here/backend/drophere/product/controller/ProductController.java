package com.drop.here.backend.drophere.product.controller;

import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.product.dto.ProductResponse;
import com.drop.here.backend.drophere.product.dto.request.ProductManagementRequest;
import com.drop.here.backend.drophere.product.service.ProductService;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequiredArgsConstructor
@RequestMapping("/companies/{companyUid}/products")
public class ProductController {
    private final ProductService productService;

    // TODO: 24/08/2020 docs dla boyss
    // TODO: 24/08/2020 test, swagger
    // TODO: 24/08/2020 filtering! (archived, unavailable)
    @GetMapping
    public Page<ProductResponse> findAll(@ApiIgnore @PathVariable String companyUid,
                                         @ApiIgnore AccountAuthentication accountAuthentication,
                                         @NotNull Pageable pageable) {
        return productService.findAll(pageable, companyUid, accountAuthentication);
    }

    // TODO: 24/08/2020 test, swagger, security (preAu)
    @PostMapping
    public ResourceOperationResponse create(@ApiIgnore @PathVariable String companyUid,
                                            @ApiIgnore AccountAuthentication accountAuthentication,
                                            @RequestBody @Valid ProductManagementRequest productManagementRequest) {
        return productService.createProduct(productManagementRequest, companyUid, accountAuthentication);
    }

    // TODO: 24/08/2020 test, swagger, security (preAu)
    @PutMapping("/{productId}")
    public ResourceOperationResponse update(@ApiIgnore @PathVariable String companyUid,
                                            @ApiIgnore @PathVariable Long productId,
                                            @ApiIgnore AccountAuthentication accountAuthentication,
                                            @RequestBody @Valid ProductManagementRequest productManagementRequest) {
        return productService.updateProduct(productManagementRequest, productId, companyUid, accountAuthentication);
    }

    // TODO: 24/08/2020 test, swagger, security (preAu)
    @DeleteMapping("/{productId}")
    public ResourceOperationResponse delete(@ApiIgnore @PathVariable String companyUid,
                                            @ApiIgnore @PathVariable Long productId,
                                            @ApiIgnore AccountAuthentication accountAuthentication) {
        return productService.deleteProduct(productId, companyUid, accountAuthentication);
    }
}
