package com.drop.here.backend.drophere.product.controller;

import com.drop.here.backend.drophere.common.exceptions.ExceptionMessage;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.product.dto.ProductResponse;
import com.drop.here.backend.drophere.product.dto.request.ProductManagementRequest;
import com.drop.here.backend.drophere.product.service.ProductService;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import com.drop.here.backend.drophere.swagger.ApiAuthorizationToken;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequiredArgsConstructor
@RequestMapping("/companies/{companyUid}/products")
public class ProductController {
    private final ProductService productService;

    // TODO: 24/08/2020 get na 1 razem z ingredientami
    // TODO: 24/08/2020 docs dla boyss
    // TODO: 24/08/2020 test
    @GetMapping
    @ApiOperation("Fetching products")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "List of products, response is list of dto", response = ProductResponse.class, responseContainer = "Page"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @ApiAuthorizationToken
    public Page<ProductResponse> findAll(@ApiIgnore @PathVariable String companyUid,
                                         @ApiIgnore AccountAuthentication accountAuthentication,
                                         @NotNull Pageable pageable) {
        return productService.findAll(pageable, companyUid, accountAuthentication);
    }

    // TODO: 24/08/2020 test
    @PostMapping
    @ApiAuthorizationToken
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Creating product")
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_CREATED, message = "Product created", response = ResourceOperationResponse.class),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @PreAuthorize("@authenticationPrivilegesService.isOwnCompanyOperation(authentication, #companyUid)")
    public ResourceOperationResponse create(@ApiIgnore @PathVariable String companyUid,
                                            @ApiIgnore AccountAuthentication authentication,
                                            @RequestBody @Valid ProductManagementRequest productManagementRequest) {
        return productService.createProduct(productManagementRequest, companyUid);
    }

    // TODO: 24/08/2020 test
    @PutMapping("/{productId}")
    @ApiAuthorizationToken
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation("Updating product")
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Product updated", response = ResourceOperationResponse.class),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @PreAuthorize("@authenticationPrivilegesService.isOwnCompanyOperation(authentication, #companyUid)")
    public ResourceOperationResponse update(@ApiIgnore @PathVariable String companyUid,
                                            @ApiIgnore @PathVariable Long productId,
                                            @ApiIgnore AccountAuthentication authentication,
                                            @RequestBody @Valid ProductManagementRequest productManagementRequest) {
        return productService.updateProduct(productManagementRequest, productId, companyUid);
    }

    // TODO: 24/08/2020 test
    @DeleteMapping("/{productId}")
    @ApiAuthorizationToken
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation("Deleting product")
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_CREATED, message = "Product deleted", response = ResourceOperationResponse.class),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @PreAuthorize("@authenticationPrivilegesService.isOwnCompanyOperation(authentication, #companyUid)")
    public ResourceOperationResponse delete(@ApiIgnore @PathVariable String companyUid,
                                            @ApiIgnore @PathVariable Long productId,
                                            @ApiIgnore AccountAuthentication authentication) {
        return productService.deleteProduct(productId, companyUid);
    }
}
