package com.drop.here.backend.drophere.customer.controller;

import com.drop.here.backend.drophere.common.exceptions.ExceptionMessage;
import com.drop.here.backend.drophere.customer.service.CustomerService;
import com.drop.here.backend.drophere.swagger.ApiAuthorizationToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customers")
@Api(tags = "Customer API")
public class CustomerController {
    private final CustomerService customerService;

    @GetMapping("/{customerId}/images")
    @ApiOperation("Get customer image")
    @ApiAuthorizationToken
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Customer image"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    public Mono<ResponseEntity<byte[]>> findImage(@ApiIgnore @PathVariable Long customerId) {
        return customerService.findImage(customerId)
                .map(image -> ResponseEntity
                        .status(HttpStatus.OK)
                        .eTag(customerId + "" + image.getId())
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(image.getBytes()));
    }
}
