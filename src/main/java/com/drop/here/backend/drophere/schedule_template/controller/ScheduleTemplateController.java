package com.drop.here.backend.drophere.schedule_template.controller;

import com.drop.here.backend.drophere.common.exceptions.ExceptionMessage;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.schedule_template.dto.ScheduleTemplateManagementRequest;
import com.drop.here.backend.drophere.schedule_template.dto.ScheduleTemplateResponse;
import com.drop.here.backend.drophere.schedule_template.dto.ScheduleTemplateShortResponse;
import com.drop.here.backend.drophere.schedule_template.service.ScheduleTemplateService;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
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
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

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
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Company's schedule templates"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @PreAuthorize("@authenticationPrivilegesService.isOwnCompanyOperation(authentication, #companyUid)")
    public List<ScheduleTemplateShortResponse> findTemplates(@ApiIgnore @PathVariable String companyUid,
                                                             @ApiIgnore AccountAuthentication accountAuthentication) {
        return scheduleTemplateService.findTemplates(accountAuthentication);
    }

    @GetMapping("/{scheduleTemplateId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@authenticationPrivilegesService.isOwnCompanyOperation(authentication, #companyUid)")
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Found schedule template"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @ApiOperation("Find schedule template")
    public ScheduleTemplateResponse findTemplate(@ApiIgnore @PathVariable String companyUid,
                                                 @ApiIgnore @PathVariable Long scheduleTemplateId,
                                                 @ApiIgnore AccountAuthentication accountAuthentication) {
        return scheduleTemplateService.findById(scheduleTemplateId, accountAuthentication);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Creating new template")
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_CREATED, message = "Schedule template created"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @PreAuthorize("@authenticationPrivilegesService.isOwnCompanyOperation(authentication, #companyUid)")
    public ResourceOperationResponse createTemplate(@ApiIgnore @PathVariable String companyUid,
                                                    @RequestBody @Valid ScheduleTemplateManagementRequest scheduleTemplateManagementRequest,
                                                    @ApiIgnore AccountAuthentication accountAuthentication) {
        return scheduleTemplateService.createTemplate(companyUid, scheduleTemplateManagementRequest, accountAuthentication);
    }

    @PutMapping("/{scheduleTemplateId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@authenticationPrivilegesService.isOwnCompanyOperation(authentication, #companyUid)")
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Schedule template updated"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @ApiOperation("Updating template")
    public ResourceOperationResponse updateTemplate(@ApiIgnore @PathVariable String companyUid,
                                                    @ApiIgnore @PathVariable Long scheduleTemplateId,
                                                    @RequestBody @Valid ScheduleTemplateManagementRequest scheduleTemplateManagementRequest,
                                                    @ApiIgnore AccountAuthentication accountAuthentication) {
        return scheduleTemplateService.updateTemplate(companyUid, scheduleTemplateId, scheduleTemplateManagementRequest, accountAuthentication);
    }

    @DeleteMapping("/{scheduleTemplateId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@authenticationPrivilegesService.isOwnCompanyOperation(authentication, #companyUid)")
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Schedule template deleted"),
            @ApiResponse(code = 403, message = "Forbidden", response = ExceptionMessage.class),
            @ApiResponse(code = 422, message = "Error", response = ExceptionMessage.class)
    })
    @ApiOperation("Deleting template")
    public ResourceOperationResponse deleteTemplate(@ApiIgnore @PathVariable String companyUid,
                                                    @ApiIgnore @PathVariable Long scheduleTemplateId,
                                                    @ApiIgnore AccountAuthentication accountAuthentication) {
        return scheduleTemplateService.deleteTemplate(companyUid, scheduleTemplateId, accountAuthentication);
    }


}
