package com.drop.here.backend.drophere.company.service;

import com.drop.here.backend.drophere.common.exceptions.RestExceptionStatusCode;
import com.drop.here.backend.drophere.common.exceptions.RestIllegalRequestValueException;
import com.drop.here.backend.drophere.company.dto.request.CompanyManagementRequest;
import com.drop.here.backend.drophere.company.enums.CompanyVisibilityStatus;
import io.vavr.control.Try;
import org.springframework.stereotype.Service;

@Service
public class CompanyValidationService {

    public void validate(CompanyManagementRequest companyManagementRequest) {
        validateVisibilityStatus(companyManagementRequest.getVisibilityStatus());
    }

    private void validateVisibilityStatus(String visibilityStatus) {
        Try.ofSupplier(() -> CompanyVisibilityStatus.valueOf(visibilityStatus))
                .getOrElseThrow(() -> new RestIllegalRequestValueException(String.format(
                        "Invalid visibility status %s", visibilityStatus),
                        RestExceptionStatusCode.INVALID_COMPANY_VISIBILITY_STATUS));
    }
}
