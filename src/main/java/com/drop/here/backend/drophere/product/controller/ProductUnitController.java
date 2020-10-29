package com.drop.here.backend.drophere.product.controller;

import com.drop.here.backend.drophere.common.exceptions.ExceptionMessage;
import com.drop.here.backend.drophere.product.dto.response.ProductUnitResponse;
import com.drop.here.backend.drophere.product.service.ProductUnitService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/units")
@RequiredArgsConstructor
@Api(tags = "Product units API")
public class ProductUnitController {
    private final ProductUnitService productUnitService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "List of units sorted by name", response = ProductUnitResponse.class, responseContainer = "List"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @ApiOperation(value = "Get all units", authorizations = @Authorization(value = "AUTHORIZATION"))
    public List<ProductUnitResponse> findAll() {
        return productUnitService.findAll();
    }
}
