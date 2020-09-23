package com.drop.here.backend.drophere.spot.controller;

import com.drop.here.backend.drophere.common.exceptions.ExceptionMessage;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.configuration.security.AccountAuthentication;
import com.drop.here.backend.drophere.spot.dto.request.SpotJoinRequest;
import com.drop.here.backend.drophere.spot.dto.request.SpotMembershipManagementRequest;
import com.drop.here.backend.drophere.spot.dto.response.SpotCustomerResponse;
import com.drop.here.backend.drophere.spot.service.SpotMembershipService;
import com.drop.here.backend.drophere.swagger.ApiAuthorizationToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/spots")
@Api(tags = "Spots user API")
public class SpotUserController {
    private final SpotMembershipService spotMembershipService;

    @ApiOperation("Listing all spots")
    @GetMapping
    @ApiAuthorizationToken
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "All filtered out spots"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    public Flux<SpotCustomerResponse> findSpots(@ApiIgnore Mono<AccountAuthentication> accountAuthenticationMono,
                                                @ApiParam(value = "Searching x coordinate", required = true) @RequestParam Double xCoordinate,
                                                @ApiParam(value = "Searching y coordinate", required = true) @RequestParam Double yCoordinate,
                                                @ApiParam(value = "Searching radius (meters)", required = true) @RequestParam Integer radius,
                                                @ApiParam(value = "Is/is not a member") @RequestParam(required = false) Boolean member,
                                                @ApiParam(value = "Name of drop/company (prefix)", required = true) @RequestParam String namePrefix,
                                                Pageable pageable) {
        return accountAuthenticationMono.flatMapMany(accountAuthentication -> spotMembershipService.findSpots(accountAuthentication, xCoordinate, yCoordinate, radius, member, namePrefix, pageable));
    }

    @ApiOperation("Joining to spot")
    @PostMapping("/{spotUid}/companies/{companyUid}/memberships")
    @ApiAuthorizationToken
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Joined to drop", response = ResourceOperationResponse.class),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @PreAuthorize("@authenticationPrivilegesService.isCompanyVisibleForCustomer(authentication, #companyUid)")
    public Mono<ResourceOperationResponse> createSpotMembership(@ApiIgnore Mono<AccountAuthentication> accountAuthenticationMono,
                                                                @ApiIgnore @PathVariable String spotUid,
                                                                @ApiIgnore @PathVariable String companyUid,
                                                                @RequestBody @Valid Mono<SpotJoinRequest> spotJoinRequestMono) {
        return accountAuthenticationMono.zipWith(spotJoinRequestMono)
                .flatMap(tuple -> spotMembershipService.createSpotMembership(tuple.getT2(), spotUid, companyUid, tuple.getT1()));
    }

    @ApiOperation("Updating spot membership")
    @PutMapping("/{spotUid}/companies/{companyUid}/memberships")
    @ApiAuthorizationToken
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Membership updated", response = ResourceOperationResponse.class),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    public Mono<ResourceOperationResponse> updateSpotMembership(@ApiIgnore Mono<AccountAuthentication> accountAuthenticationMono,
                                                                @ApiIgnore @PathVariable String spotUid,
                                                                @ApiIgnore @PathVariable String companyUid,
                                                                @RequestBody @Valid Mono<SpotMembershipManagementRequest> spotMembershipManagementRequestMono) {
        return accountAuthenticationMono.zipWith(spotMembershipManagementRequestMono)
                .flatMap(tuple -> spotMembershipService.updateSpotMembership(tuple.getT2(), spotUid, companyUid, tuple.getT1()));
    }


    @ApiOperation("Leaving drop")
    @DeleteMapping("/{spotUid}/companies/{companyUid}/memberships")
    @ApiAuthorizationToken
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Left from drop", response = ResourceOperationResponse.class),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    public Mono<ResourceOperationResponse> deleteSpot(@ApiIgnore Mono<AccountAuthentication> accountAuthenticationMono,
                                                      @ApiIgnore @PathVariable String spotUid,
                                                      @ApiIgnore @PathVariable String companyUid) {
        return accountAuthenticationMono.flatMap(accountAuthentication -> spotMembershipService.deleteSpotMembership(spotUid, companyUid, accountAuthentication));
    }
}
