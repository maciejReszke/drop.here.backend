package com.drop.here.backend.drophere.company.service;

import com.drop.here.backend.drophere.common.exceptions.RestIllegalRequestValueException;
import com.drop.here.backend.drophere.company.dto.request.CompanyManagementRequest;
import com.drop.here.backend.drophere.test_data.CompanyDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(MockitoExtension.class)
class CompanyValidationServiceTest {

    @InjectMocks
    private CompanyValidationService companyValidationService;

    @Test
    void givenValidRequestWhenValidateThenDoNothing() {
        //given
        final CompanyManagementRequest request = CompanyDataGenerator.managementRequest(1);

        //when
        final Throwable throwable = catchThrowable(() -> companyValidationService.validate(request));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenInvalidRequestWhenValidateThenDoThrowException() {
        //given
        final CompanyManagementRequest request = CompanyDataGenerator.managementRequest(1);
        request.setVisibilityStatus("ninja");

        //when
        final Throwable throwable = catchThrowable(() -> companyValidationService.validate(request));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
    }

}