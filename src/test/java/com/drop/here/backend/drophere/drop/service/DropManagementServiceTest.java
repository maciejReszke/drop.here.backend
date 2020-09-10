package com.drop.here.backend.drophere.drop.service;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.common.exceptions.RestEntityNotFoundException;
import com.drop.here.backend.drophere.common.rest.ResourceOperationResponse;
import com.drop.here.backend.drophere.common.rest.ResourceOperationStatus;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.drop.dto.DropCompanyMembershipManagementRequest;
import com.drop.here.backend.drophere.drop.dto.request.DropManagementRequest;
import com.drop.here.backend.drophere.drop.dto.response.DropCompanyMembershipResponse;
import com.drop.here.backend.drophere.drop.dto.response.DropCompanyResponse;
import com.drop.here.backend.drophere.drop.entity.Drop;
import com.drop.here.backend.drophere.drop.repository.DropRepository;
import com.drop.here.backend.drophere.security.configuration.AccountAuthentication;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import com.drop.here.backend.drophere.test_data.AuthenticationDataGenerator;
import com.drop.here.backend.drophere.test_data.DropDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DropManagementServiceTest {

    @InjectMocks
    private DropManagementService dropManagementService;

    @Mock
    private DropMappingService dropMappingService;

    @Mock
    private DropRepository dropRepository;

    @Mock
    private DropManagementValidationService dropManagementValidationService;

    @Mock
    private DropMembershipService dropMembershipService;

    @Test
    void givenDropManagementRequestWhenCreateDropThenCreate() {
        //given
        final DropManagementRequest dropManagementRequest = DropManagementRequest.builder().build();
        final String companyUid = "companyUid";

        doNothing().when(dropManagementValidationService).validateDropRequest(dropManagementRequest);
        final Company company = Company.builder().build();
        final Drop drop = DropDataGenerator.drop(1, company);
        final Account account = AccountDataGenerator.companyAccount(1);
        final AccountAuthentication accountAuthentication = AuthenticationDataGenerator.accountAuthentication(account);
        when(dropMappingService.toEntity(dropManagementRequest, accountAuthentication)).thenReturn(drop);
        when(dropRepository.save(drop)).thenReturn(drop);

        //when
        final ResourceOperationResponse response = dropManagementService.createDrop(dropManagementRequest, companyUid, accountAuthentication);

        //then
        assertThat(response.getOperationStatus()).isEqualTo(ResourceOperationStatus.CREATED);
    }

    @Test
    void givenExistingDropAndDropManagementRequestWhenUpdateDropThenUpdate() {
        //given
        final DropManagementRequest dropManagementRequest = DropManagementRequest.builder().build();
        final String companyUid = "companyUid";

        doNothing().when(dropManagementValidationService).validateDropRequest(dropManagementRequest);
        final Company company = Company.builder().build();
        final Drop drop = DropDataGenerator.drop(1, company);
        final Long dropId = 1L;
        when(dropRepository.findByIdAndCompanyUid(dropId, companyUid)).thenReturn(Optional.of(drop));
        doNothing().when(dropMappingService).update(drop, dropManagementRequest);
        when(dropRepository.save(drop)).thenReturn(drop);

        //when
        final ResourceOperationResponse response = dropManagementService.updateDrop(dropManagementRequest, dropId, companyUid);

        //then
        assertThat(response.getOperationStatus()).isEqualTo(ResourceOperationStatus.UPDATED);
    }


    @Test
    void givenNotExistingDropAndDropManagementRequestWhenUpdateDropThenError() {
        //given
        final DropManagementRequest dropManagementRequest = DropManagementRequest.builder().build();
        final String companyUid = "companyUid";

        final Long dropId = 1L;
        when(dropRepository.findByIdAndCompanyUid(dropId, companyUid)).thenReturn(Optional.empty());

        //when
        final Throwable throwable = catchThrowable(() -> dropManagementService.updateDrop(dropManagementRequest, dropId, companyUid));

        //then
        assertThat(throwable).isInstanceOf(RestEntityNotFoundException.class);
    }

    @Test
    void givenExistingDropWhenDeleteDropThenDelete() {
        //given
        final String companyUid = "companyUid";

        final Company company = Company.builder().build();
        final Drop drop = DropDataGenerator.drop(1, company);
        final Long dropId = 1L;
        when(dropRepository.findByIdAndCompanyUid(dropId, companyUid)).thenReturn(Optional.of(drop));
        doNothing().when(dropRepository).delete(drop);
        doNothing().when(dropMembershipService).deleteMemberships(drop);

        //when
        final ResourceOperationResponse response = dropManagementService.deleteDrop(dropId, companyUid);

        //then
        assertThat(response.getOperationStatus()).isEqualTo(ResourceOperationStatus.DELETED);
    }


    @Test
    void givenNotExistingDropWhenDeleteDropThenError() {
        //given
        final String companyUid = "companyUid";

        final Long dropId = 1L;
        when(dropRepository.findByIdAndCompanyUid(dropId, companyUid)).thenReturn(Optional.empty());

        //when
        final Throwable throwable = catchThrowable(() -> dropManagementService.deleteDrop(dropId, companyUid));

        //then
        assertThat(throwable).isInstanceOf(RestEntityNotFoundException.class);
    }

    @Test
    void givenCompanyUidAndNameWhenFindCompanyDropsThenFind() {
        //given
        final String companyUid = "companyUid";
        final String name = "name";

        final Drop drop = Drop.builder().build();
        final DropCompanyResponse response = DropCompanyResponse.builder().build();
        when(dropRepository.findAllByCompanyUidAndNameStartsWith(companyUid, name)).thenReturn(List.of(drop));
        when(dropMappingService.toDropCompanyResponse(drop)).thenReturn(response);
        //when
        final List<DropCompanyResponse> result = dropManagementService.findCompanyDrops(companyUid, name);

        //then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(response);
    }

    @Test
    void givenExistingDropWhenFindMembershipsThenFind() {
        //given
        final Long dropId = 1L;
        final String companyUid = "companyUid";
        final String desiredCustomerSubstring = "desiredCustomerSubstring";
        final String membershipStatus = "membershipStatus";
        final Pageable pageable = Pageable.unpaged();

        final Drop drop = DropDataGenerator.drop(1, null);
        final Page<DropCompanyMembershipResponse> page = Page.empty();
        when(dropRepository.findByIdAndCompanyUid(dropId, companyUid)).thenReturn(Optional.of(drop));
        when(dropMembershipService.findMemberships(drop, desiredCustomerSubstring, membershipStatus, pageable))
                .thenReturn(page);

        //when
        final Page<DropCompanyMembershipResponse> response = dropManagementService.findMemberships(dropId, companyUid, desiredCustomerSubstring, membershipStatus, pageable);

        //then
        assertThat(response).isEqualTo(page);
    }

    @Test
    void givenNotExistingDropWhenFindMembershipsThenError() {
        //given
        final Long dropId = 1L;
        final String companyUid = "companyUid";
        final String desiredCustomerSubstring = "desiredCustomerSubstring";
        final String membershipStatus = "membershipStatus";
        final Pageable pageable = Pageable.unpaged();

        when(dropRepository.findByIdAndCompanyUid(dropId, companyUid)).thenReturn(Optional.empty());

        //when
        final Throwable throwable = catchThrowable(() -> dropManagementService.findMemberships(dropId, companyUid, desiredCustomerSubstring, membershipStatus, pageable));

        //then
        assertThat(throwable).isInstanceOf(RestEntityNotFoundException.class);
    }

    @Test
    void givenExistingDropWhenUpdateMembershipsThenAccept() {
        //given
        final Long dropId = 1L;
        final String companyUid = "companyUid";
        final Long membershipId = 2L;
        final ResourceOperationResponse resourceOperationResponse = new ResourceOperationResponse(ResourceOperationStatus.UPDATED, 1L);
        final DropCompanyMembershipManagementRequest dropCompanyMembershipManagementRequest = DropCompanyMembershipManagementRequest.builder().build();

        final Drop drop = DropDataGenerator.drop(1, null);
        when(dropRepository.findByIdAndCompanyUid(dropId, companyUid)).thenReturn(Optional.of(drop));
        when(dropMembershipService.updateMembership(drop, membershipId, dropCompanyMembershipManagementRequest)).thenReturn(resourceOperationResponse);

        //when
        final ResourceOperationResponse response = dropManagementService.updateMembership(dropId, companyUid, membershipId, dropCompanyMembershipManagementRequest);

        //then
        assertThat(response).isEqualTo(resourceOperationResponse);
    }

    @Test
    void givenNotExistingDropWhenUpdateMembershipsThenError() {
        //given
        final Long dropId = 1L;
        final String companyUid = "companyUid";
        final Long membershipId = 2L;
        when(dropRepository.findByIdAndCompanyUid(dropId, companyUid)).thenReturn(Optional.empty());
        final DropCompanyMembershipManagementRequest dropCompanyMembershipManagementRequest = DropCompanyMembershipManagementRequest.builder().build();

        //when
        final Throwable throwable = catchThrowable(() -> dropManagementService.updateMembership(dropId, companyUid, membershipId, dropCompanyMembershipManagementRequest));

        //then
        assertThat(throwable).isInstanceOf(RestEntityNotFoundException.class);
    }
}