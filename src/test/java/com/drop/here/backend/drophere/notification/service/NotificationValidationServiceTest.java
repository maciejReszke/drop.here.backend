package com.drop.here.backend.drophere.notification.service;

import com.drop.here.backend.drophere.authentication.authentication.dto.request.ExternalAuthenticationProviderLoginRequest;
import com.drop.here.backend.drophere.authentication.authentication.service.external_provider.ExternalAuthenticationValidationService;
import com.drop.here.backend.drophere.common.exceptions.RestIllegalRequestValueException;
import com.drop.here.backend.drophere.notification.dto.NotificationManagementRequest;
import com.drop.here.backend.drophere.notification.enums.NotificationReadStatus;
import com.drop.here.backend.drophere.test_data.ExternalAuthenticationDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(MockitoExtension.class)
class NotificationValidationServiceTest {

    @InjectMocks
    private NotificationValidationService notificationValidationService;

    @Test
    void givenValidRequestWhenValidateUpdateNotificationRequestThenDoNothing() {
        //given
        final NotificationManagementRequest managementRequest = NotificationManagementRequest
                .builder()
                .readStatus(NotificationReadStatus.READ.name())
                .build();

        //when
        final Throwable throwable = catchThrowable(() -> notificationValidationService.validateUpdateNotificationRequest(managementRequest));

        //then
        assertThat(throwable).isNull();
    }

    @Test
    void givenInvalidRequestWhenValidateUpdateNotificationRequestThenDoNothing() {
        //given
        final NotificationManagementRequest managementRequest = NotificationManagementRequest
                .builder()
                .readStatus(NotificationReadStatus.READ.name() + "kaka")
                .build();

        //when
        final Throwable throwable = catchThrowable(() -> notificationValidationService.validateUpdateNotificationRequest(managementRequest));

        //then
        assertThat(throwable).isInstanceOf(RestIllegalRequestValueException.class);
    }

}