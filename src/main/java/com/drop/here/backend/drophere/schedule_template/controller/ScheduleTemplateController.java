package com.drop.here.backend.drophere.schedule_template.controller;

import com.drop.here.backend.drophere.common.exceptions.ExceptionMessage;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.configuration.security.AccountAuthentication;
import com.drop.here.backend.drophere.schedule_template.dto.ScheduleTemplateManagementRequest;
import com.drop.here.backend.drophere.schedule_template.dto.ScheduleTemplateResponse;
import com.drop.here.backend.drophere.schedule_template.dto.ScheduleTemplateShortResponse;
import com.drop.here.backend.drophere.schedule_template.service.ScheduleTemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/companies/{companyUid}/schedule_templates")
@Api(tags = "Schedule templates API")
public class ScheduleTemplateController {
    private final ScheduleTemplateService scheduleTemplateService;

    @ApiOperation("Find templates")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Company's schedule templates"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @PreAuthorize("@authenticationPrivilegesService.isOwnCompanyOperation(authentication, #companyUid)")
    public Flux<ScheduleTemplateShortResponse> findTemplates(@ApiIgnore @PathVariable String companyUid,
                                                             @ApiIgnore Mono<AccountAuthentication> accountAuthenticationMono) {
        return accountAuthenticationMono.flatMapMany(scheduleTemplateService::findTemplates);
    }

    @GetMapping("/{scheduleTemplateId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@authenticationPrivilegesService.isOwnCompanyOperation(authentication, #companyUid)")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Found schedule template"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @ApiOperation("Find schedule template")
    public Mono<ScheduleTemplateResponse> findTemplate(@ApiIgnore @PathVariable String companyUid,
                                                       @ApiIgnore @PathVariable Long scheduleTemplateId,
                                                       @ApiIgnore Mono<AccountAuthentication> accountAuthenticationMono) {
        return accountAuthenticationMono.flatMap(accountAuthentication -> scheduleTemplateService.findById(scheduleTemplateId, accountAuthentication));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Creating new template")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Schedule template created"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @PreAuthorize("@authenticationPrivilegesService.isOwnCompanyOperation(authentication, #companyUid)")
    public Mono<ResourceOperationResponse> createTemplate(@ApiIgnore @PathVariable String companyUid,
                                                          @RequestBody @Valid Mono<ScheduleTemplateManagementRequest> scheduleTemplateManagementRequestMono,
                                                          @ApiIgnore Mono<AccountAuthentication> accountAuthenticationMono) {
        return scheduleTemplateManagementRequestMono.zipWith(accountAuthenticationMono)
                .flatMap(tuple -> scheduleTemplateService.createTemplate(companyUid, tuple.getT1(), tuple.getT2()));
    }

    @PutMapping("/{scheduleTemplateId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@authenticationPrivilegesService.isOwnCompanyOperation(authentication, #companyUid)")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Schedule template updated"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @ApiOperation("Updating template")
    public Mono<ResourceOperationResponse> updateTemplate(@ApiIgnore @PathVariable String companyUid,
                                                          @ApiIgnore @PathVariable Long scheduleTemplateId,
                                                          @RequestBody @Valid Mono<ScheduleTemplateManagementRequest> scheduleTemplateManagementRequestMono,
                                                          @ApiIgnore Mono<AccountAuthentication> accountAuthenticationMono) {
        return scheduleTemplateManagementRequestMono.zipWith(accountAuthenticationMono)
                .flatMap(tuple -> scheduleTemplateService.updateTemplate(companyUid, scheduleTemplateId, tuple.getT1(), tuple.getT2()));
    }

    @DeleteMapping("/{scheduleTemplateId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@authenticationPrivilegesService.isOwnCompanyOperation(authentication, #companyUid)")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Schedule template deleted"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @ApiOperation("Deleting template")
    public Mono<ResourceOperationResponse> deleteTemplate(@ApiIgnore @PathVariable String companyUid,
                                                          @ApiIgnore @PathVariable Long scheduleTemplateId,
                                                          @ApiIgnore Mono<AccountAuthentication> accountAuthenticationMono) {
        return accountAuthenticationMono.flatMap(accountAuthentication -> scheduleTemplateService.deleteTemplate(companyUid, scheduleTemplateId, accountAuthentication));
    }


}
