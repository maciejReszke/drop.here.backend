package com.drop.here.backend.drophere.customer.controller;

import com.drop.here.backend.drophere.common.exceptions.ExceptionMessage;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.customer.dto.CustomerManagementRequest;
import com.drop.here.backend.drophere.customer.dto.CustomerManagementResponse;
import com.drop.here.backend.drophere.customer.service.CustomerService;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/management/customers")
@Api(tags = "Customer management API")
public class CustomerManagementController {
    private final CustomerService customerService;

    private static final String IMAGE_PART_NAME = "image";

    @GetMapping
    @ApiOperation(value = "Get own customer info", authorizations = @Authorization(value = "AUTHORIZATION"))
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Own customer info", response = ResourceOperationResponse.class),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    public CustomerManagementResponse findOwnCompany(@ApiIgnore AccountAuthentication authentication) {
        return customerService.findOwnCustomer(authentication);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Update customer", authorizations = @Authorization(value = "AUTHORIZATION"))
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Customer updated", response = ResourceOperationResponse.class),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    public ResourceOperationResponse updateCompany(@ApiIgnore AccountAuthentication authentication,
                                                   @RequestBody @Valid CustomerManagementRequest customerManagementRequest) {
        return customerService.updateCustomer(customerManagementRequest, authentication);
    }

    @PostMapping("/images")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Update image", authorizations = @Authorization(value = "AUTHORIZATION"))
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Image updated", response = ResourceOperationResponse.class),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    public ResourceOperationResponse updateImage(@ApiIgnore AccountAuthentication authentication,
                                                 @RequestPart(name = IMAGE_PART_NAME) MultipartFile image) {
        return customerService.updateImage(image, authentication);
    }
}
