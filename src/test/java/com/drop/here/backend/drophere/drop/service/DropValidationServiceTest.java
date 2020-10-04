package com.drop.here.backend.drophere.drop.service;

import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.authentication.account.enums.AccountProfileType;
import com.drop.here.backend.drophere.common.exceptions.RestOperationForbiddenException;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.route.repository.RouteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DropValidationServiceTest {
    @InjectMocks
    private DropValidationService dropValidationService;

    @Mock
    private RouteRepository routeRepository;

    @Test
    void givenCompanyMainProfileWhenValidateUpdateThenDoNothing() {
        //given
        final Drop drop = Drop.builder().build();
        final AccountProfile accountProfile = AccountProfile.builder().profileType(AccountProfileType.MAIN).build();

        //when
        dropValidationService.validateUpdate(drop, accountProfile);

        //then
        verifyNoMoreInteractions(routeRepository);
    }

    @Test
    void givenDropSellerWhenValidateUpdateThenDoNothing() {
        //given
        final Drop drop = Drop.builder().build();
        final AccountProfile accountProfile = AccountProfile.builder().profileType(AccountProfileType.SUBPROFILE).build();

        when(routeRepository.existsByProfileAndContainsDrop(accountProfile, drop)).thenReturn(true);
        //when
        dropValidationService.validateUpdate(drop, accountProfile);

        //then
        verifyNoMoreInteractions(routeRepository);
    }

    @Test
    void givenInvalidProfileWhenValidateUpdateThenThrowException() {
        //given
        final Drop drop = Drop.builder().build();
        final AccountProfile accountProfile = AccountProfile.builder().profileType(AccountProfileType.SUBPROFILE).build();

        when(routeRepository.existsByProfileAndContainsDrop(accountProfile, drop)).thenReturn(false);
        //when
        final Throwable throwable = catchThrowable(() -> dropValidationService.validateUpdate(drop, accountProfile));

        //then
        assertThat(throwable).isInstanceOf(RestOperationForbiddenException.class);
    }
}