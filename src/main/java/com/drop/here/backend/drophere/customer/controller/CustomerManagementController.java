package com.drop.here.backend.drophere.customer.controller;

import com.drop.here.backend.drophere.common.exceptions.ExceptionMessage;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.configuration.security.AccountAuthentication;
import com.drop.here.backend.drophere.customer.dto.CustomerManagementRequest;
import com.drop.here.backend.drophere.customer.dto.CustomerManagementResponse;
import com.drop.here.backend.drophere.customer.service.CustomerService;
import com.drop.here.backend.drophere.swagger.ApiAuthorizationToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/management/customers")
@Api(tags = "Customer management API")
public class CustomerManagementController {
    private final CustomerService customerService;

    private static final String IMAGE_PART_NAME = "image";

    @GetMapping
    @ApiOperation("Get own customer info")
    @ApiAuthorizationToken
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Own customer info", response = ResourceOperationResponse.class),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    public Mono<CustomerManagementResponse> findOwnCompany(@ApiIgnore Mono<AccountAuthentication> authenticationMono) {
        return authenticationMono.flatMap(customerService::findOwnCustomer);
    }

    @PutMapping
    @ApiAuthorizationToken
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation("Update customer")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Customer updated", response = ResourceOperationResponse.class),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    public Mono<ResourceOperationResponse> updateCompany(@ApiIgnore Mono<AccountAuthentication> authenticationMono,
                                                         @RequestBody @Valid Mono<CustomerManagementRequest> customerManagementRequestMono) {
        return authenticationMono.zipWith(customerManagementRequestMono)
                .flatMap(tuple -> customerService.updateCustomer(tuple.getT2(), tuple.getT1()));
    }

    @PostMapping("/images")
    @ApiAuthorizationToken
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation("Update image")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Image updated", response = ResourceOperationResponse.class),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    public Mono<ResourceOperationResponse> updateImage(@ApiIgnore Mono<AccountAuthentication> authenticationMono,
                                                       @RequestPart(name = IMAGE_PART_NAME) Mono<FilePart> imageMono) {
        return authenticationMono.zipWith(imageMono)
                .flatMap(tuple -> customerService.updateImage(tuple.getT2(), tuple.getT1()));
    }
}
