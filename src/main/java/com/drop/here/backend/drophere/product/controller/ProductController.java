package com.drop.here.backend.drophere.product.controller;

import com.drop.here.backend.drophere.common.exceptions.ExceptionMessage;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.product.dto.request.ProductCustomizationWrapperRequest;
import com.drop.here.backend.drophere.product.dto.request.ProductManagementRequest;
import com.drop.here.backend.drophere.product.dto.response.ProductResponse;
import com.drop.here.backend.drophere.product.service.ProductService;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import com.drop.here.backend.drophere.swagger.ApiAuthorizationToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequiredArgsConstructor
@RequestMapping("/companies/{companyUid}/products")
@Api(tags = "Products management API")
public class ProductController {
    private final ProductService productService;

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
                                         @ApiIgnore AccountAuthentication authentication,
                                         @ApiParam(value = "Desired category (1... n)") @RequestParam(value = "category", required = false) String[] desiredCategories,
                                         @ApiParam(value = "Product name (substring)") @RequestParam(value = "name", required = false) String desiredNameSubstring,
                                         @NotNull Pageable pageable) {
        return productService.findAll(pageable, companyUid, desiredCategories, desiredNameSubstring, authentication);
    }

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
    public ResourceOperationResponse createProduct(@ApiIgnore @PathVariable String companyUid,
                                                   @ApiIgnore AccountAuthentication authentication,
                                                   @RequestBody @Valid ProductManagementRequest productManagementRequest) {
        return productService.createProduct(productManagementRequest, companyUid, authentication);
    }

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
    public ResourceOperationResponse updateProduct(@ApiIgnore @PathVariable String companyUid,
                                                   @ApiIgnore @PathVariable Long productId,
                                                   @ApiIgnore AccountAuthentication authentication,
                                                   @RequestBody @Valid ProductManagementRequest productManagementRequest) {
        return productService.updateProduct(productManagementRequest, productId, companyUid);
    }

    @DeleteMapping("/{productId}")
    @ApiAuthorizationToken
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation("Deleting product")
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Product deleted", response = ResourceOperationResponse.class),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @PreAuthorize("@authenticationPrivilegesService.isOwnCompanyOperation(authentication, #companyUid)")
    public ResourceOperationResponse deleteProduct(@ApiIgnore @PathVariable String companyUid,
                                                   @ApiIgnore @PathVariable Long productId,
                                                   @ApiIgnore AccountAuthentication authentication) {
        return productService.deleteProduct(productId, companyUid);
    }

    @PostMapping("/{productId}/customizations")
    @ApiAuthorizationToken
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Creating customizations wrapper")
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_CREATED, message = "Customizations created", response = ResourceOperationResponse.class),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @PreAuthorize("@authenticationPrivilegesService.isOwnCompanyOperation(authentication, #companyUid)")
    public ResourceOperationResponse createCustomizationsWrapper(@ApiIgnore @PathVariable String companyUid,
                                                                 @ApiIgnore @PathVariable Long productId,
                                                                 @ApiIgnore AccountAuthentication authentication,
                                                                 @RequestBody @Valid ProductCustomizationWrapperRequest productCustomizationWrapperRequest) {
        return productService.createCustomization(productId, companyUid, productCustomizationWrapperRequest, authentication);
    }

    @PutMapping("/{productId}/customizations/{customizationId}")
    @ApiAuthorizationToken
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation("Updating customizations wrapper")
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Customizations updated", response = ResourceOperationResponse.class),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @PreAuthorize("@authenticationPrivilegesService.isOwnCompanyOperation(authentication, #companyUid)")
    public ResourceOperationResponse updateCustomizationsWrapper(@ApiIgnore @PathVariable String companyUid,
                                                                 @ApiIgnore @PathVariable Long productId,
                                                                 @ApiIgnore @PathVariable Long customizationId,
                                                                 @ApiIgnore AccountAuthentication authentication,
                                                                 @RequestBody @Valid ProductCustomizationWrapperRequest productCustomizationWrapperRequest) {
        return productService.updateCustomization(productId, companyUid, customizationId, productCustomizationWrapperRequest, authentication);
    }

    @DeleteMapping("/{productId}/customizations/{customizationId}")
    @ApiAuthorizationToken
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation("Deleting customizations wrapper")
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Customizations deleted", response = ResourceOperationResponse.class),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @PreAuthorize("@authenticationPrivilegesService.isOwnCompanyOperation(authentication, #companyUid)")
    public ResourceOperationResponse deleteCustomizationsWrapper(@ApiIgnore @PathVariable String companyUid,
                                                                 @ApiIgnore @PathVariable Long productId,
                                                                 @ApiIgnore @PathVariable Long customizationId,
                                                                 @ApiIgnore AccountAuthentication authentication) {
        return productService.deleteCustomization(productId, companyUid, customizationId, authentication);
    }
}
