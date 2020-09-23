package com.drop.here.backend.drophere.product.controller;

import com.drop.here.backend.drophere.common.exceptions.ExceptionMessage;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.configuration.security.AccountAuthentication;
import com.drop.here.backend.drophere.product.dto.request.ProductCustomizationWrapperRequest;
import com.drop.here.backend.drophere.product.dto.request.ProductManagementRequest;
import com.drop.here.backend.drophere.product.dto.response.ProductResponse;
import com.drop.here.backend.drophere.product.service.ProductService;
import com.drop.here.backend.drophere.swagger.ApiAuthorizationToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequiredArgsConstructor
@RequestMapping("/companies/{companyUid}/products")
@Api(tags = "Products management API")
public class ProductController {
    private final ProductService productService;

    private static final String IMAGE_PART_NAME = "image";

    @GetMapping
    @ApiOperation("Fetching products")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of products, response is list of dto", response = ProductResponse.class, responseContainer = "Page"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @ApiAuthorizationToken
    @PreAuthorize("@authenticationPrivilegesService.isOwnCompanyOperation(authentication, #companyUid) or " +
            "@authenticationPrivilegesService.isCompanyVisibleForCustomer(authentication, #companyUid)")
    public Flux<ProductResponse> findAll(@ApiIgnore @PathVariable String companyUid,
                                         @ApiIgnore Mono<AccountAuthentication> accountAuthenticationMono,
                                         @ApiParam(value = "Desired category (1... n)") @RequestParam(value = "category", required = false) String[] desiredCategories,
                                         @ApiParam(value = "Product name (substring)") @RequestParam(value = "name", required = false) String desiredNameSubstring,
                                         @NotNull Pageable pageable) {
        return accountAuthenticationMono.flatMapMany(accountAuthentication -> productService.findAll(pageable, companyUid, desiredCategories, desiredNameSubstring, accountAuthentication));
    }

    @PostMapping
    @ApiAuthorizationToken
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Creating product")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Product created", response = ResourceOperationResponse.class),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @PreAuthorize("@authenticationPrivilegesService.isOwnCompanyOperation(authentication, #companyUid)")
    public Mono<ResourceOperationResponse> createProduct(@ApiIgnore @PathVariable String companyUid,
                                                         @ApiIgnore Mono<AccountAuthentication> accountAuthenticationMono,
                                                         @RequestBody @Valid Mono<ProductManagementRequest> productManagementRequestMono) {
        return accountAuthenticationMono.zipWith(productManagementRequestMono)
                .flatMap(tuple -> productService.createProduct(tuple.getT2(), companyUid, tuple.getT1()));
    }

    @PutMapping("/{productId}")
    @ApiAuthorizationToken
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation("Updating product")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Product updated", response = ResourceOperationResponse.class),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @PreAuthorize("@authenticationPrivilegesService.isOwnCompanyOperation(authentication, #companyUid)")
    public Mono<ResourceOperationResponse> updateProduct(@ApiIgnore @PathVariable String companyUid,
                                                         @ApiIgnore @PathVariable Long productId,
                                                         @ApiIgnore Mono<AccountAuthentication> accountAuthenticationMono,
                                                         @RequestBody @Valid Mono<ProductManagementRequest> productManagementRequestMono) {
        return accountAuthenticationMono.zipWith(productManagementRequestMono)
                .flatMap(tuple -> productService.updateProduct(tuple.getT2(), productId, companyUid));
    }

    @DeleteMapping("/{productId}")
    @ApiAuthorizationToken
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation("Deleting product")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Product deleted", response = ResourceOperationResponse.class),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @PreAuthorize("@authenticationPrivilegesService.isOwnCompanyOperation(authentication, #companyUid)")
    public Mono<ResourceOperationResponse> deleteProduct(@ApiIgnore @PathVariable String companyUid,
                                                         @ApiIgnore @PathVariable Long productId,
                                                         @ApiIgnore Mono<AccountAuthentication> authentication) {
        return authentication.flatMap(ignore -> productService.deleteProduct(productId, companyUid));
    }

    @PostMapping("/{productId}/images")
    @ApiAuthorizationToken
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation("Update product image")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Image updated", response = ResourceOperationResponse.class),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @PreAuthorize("@authenticationPrivilegesService.isOwnCompanyOperation(authentication, #companyUid)")
    public Mono<ResourceOperationResponse> updateImage(@ApiIgnore Mono<AccountAuthentication> accountAuthenticationMono,
                                                       @ApiIgnore @PathVariable String companyUid,
                                                       @ApiIgnore @PathVariable Long productId,
                                                       @RequestPart(name = IMAGE_PART_NAME) MultipartFile image) {
        return accountAuthenticationMono.flatMap(accountAuthentication -> productService.updateImage(productId, companyUid, image, accountAuthentication));
    }

    @GetMapping("/{productId}/images")
    @ApiOperation("Get product image")
    @ApiAuthorizationToken
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Product image"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    public Mono<ResponseEntity<byte[]>> findImage(@ApiIgnore @PathVariable String companyUid,
                                                  @ApiIgnore @PathVariable Long productId) {
        return productService.findImage(productId, companyUid)
                .map(image -> ResponseEntity
                        .status(HttpStatus.OK)
                        .eTag(productId + "" + image.getId())
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(image.getBytes()));
    }

    @PostMapping("/{productId}/customizations")
    @ApiAuthorizationToken
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Creating customizations wrapper")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Customizations created", response = ResourceOperationResponse.class),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @PreAuthorize("@authenticationPrivilegesService.isOwnCompanyOperation(authentication, #companyUid)")
    public Mono<ResourceOperationResponse> createCustomizationsWrapper(@ApiIgnore @PathVariable String companyUid,
                                                                       @ApiIgnore @PathVariable Long productId,
                                                                       @ApiIgnore Mono<AccountAuthentication> accountAuthenticationMono,
                                                                       @RequestBody @Valid Mono<ProductCustomizationWrapperRequest> productCustomizationWrapperRequestMono) {
        return accountAuthenticationMono.zipWith(productCustomizationWrapperRequestMono)
                .flatMap(tuple -> productService.createCustomization(productId, companyUid, tuple.getT2(), tuple.getT1()));
    }

    @PutMapping("/{productId}/customizations/{customizationId}")
    @ApiAuthorizationToken
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation("Updating customizations wrapper")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Customizations updated", response = ResourceOperationResponse.class),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @PreAuthorize("@authenticationPrivilegesService.isOwnCompanyOperation(authentication, #companyUid)")
    public Mono<ResourceOperationResponse> updateCustomizationsWrapper(@ApiIgnore @PathVariable String companyUid,
                                                                       @ApiIgnore @PathVariable Long productId,
                                                                       @ApiIgnore @PathVariable Long customizationId,
                                                                       @ApiIgnore Mono<AccountAuthentication> authenticationMono,
                                                                       @RequestBody @Valid Mono<ProductCustomizationWrapperRequest> productCustomizationWrapperRequestMono) {
        return authenticationMono.zipWith(productCustomizationWrapperRequestMono)
                .flatMap(tuple -> productService.updateCustomization(productId, companyUid, customizationId, tuple.getT2(), tuple.getT1()));
    }

    @DeleteMapping("/{productId}/customizations/{customizationId}")
    @ApiAuthorizationToken
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation("Deleting customizations wrapper")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Customizations deleted", response = ResourceOperationResponse.class),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @PreAuthorize("@authenticationPrivilegesService.isOwnCompanyOperation(authentication, #companyUid)")
    public Mono<ResourceOperationResponse> deleteCustomizationsWrapper(@ApiIgnore @PathVariable String companyUid,
                                                                       @ApiIgnore @PathVariable Long productId,
                                                                       @ApiIgnore @PathVariable Long customizationId,
                                                                       @ApiIgnore Mono<AccountAuthentication> authenticationMono) {
        return authenticationMono.flatMap(accountAuthentication -> productService.deleteCustomization(productId, companyUid, customizationId, accountAuthentication));
    }
}
