package com.drop.here.backend.drophere.product.controller;

import com.drop.here.backend.drophere.common.exceptions.ExceptionMessage;
import com.drop.here.backend.drophere.product.dto.response.ProductCategoryResponse;
import com.drop.here.backend.drophere.product.service.ProductCategoryService;
import com.drop.here.backend.drophere.swagger.ApiAuthorizationToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@RequestMapping("/companies/{companyUid}/categories")
@Api(tags = "Product category API")
public class ProductCategoryController {
    private final ProductCategoryService productCategoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ApiAuthorizationToken
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of categories sorted by name", response = ProductCategoryResponse.class, responseContainer = "List"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @ApiOperation("Get all categories for given company")
    public Flux<ProductCategoryResponse> findAll(@PathVariable String companyUid) {
        return productCategoryService.findAll(companyUid);
    }
}
