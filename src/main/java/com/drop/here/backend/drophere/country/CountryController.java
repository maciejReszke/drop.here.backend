package com.drop.here.backend.drophere.country;

import com.drop.here.backend.drophere.common.exceptions.ExceptionMessage;
import com.drop.here.backend.drophere.swagger.ApiAuthorizationToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/countries")
@RequiredArgsConstructor
@Api(tags = "Country API")
public class CountryController {
    private final CountryService countryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ApiAuthorizationToken
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of countries sorted by name", response = CountryResponse.class, responseContainer = "List"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @ApiOperation("Get all active countries")
    public Flux<CountryResponse> findAllActive() {
        return countryService.findAllActive();
    }
}
