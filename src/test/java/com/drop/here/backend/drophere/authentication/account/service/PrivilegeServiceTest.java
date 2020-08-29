package com.drop.here.backend.drophere.authentication.account.service;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.authentication.account.entity.Privilege;
import com.drop.here.backend.drophere.authentication.account.enums.AccountProfileType;
import com.drop.here.backend.drophere.authentication.account.enums.AccountType;
import com.drop.here.backend.drophere.authentication.account.repository.PrivilegeRepository;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import com.drop.here.backend.drophere.test_data.AccountProfileDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PrivilegeServiceTest {

    @InjectMocks
    private PrivilegeService privilegeService;

    @Mock
    private PrivilegeRepository privilegeRepository;

    @Test
    void givenCompanyAccountWhenAddNewAccountPrivilegesThenCreateSaveAndAdd() {
        //given
        final Account account = AccountDataGenerator.companyAccount(1);
        account.setAccountType(AccountType.COMPANY);

        when(privilegeRepository.save(any())).thenAnswer(invocation -> invocation.getArguments()[0]);

        //when
        privilegeService.addNewAccountPrivileges(account);

        //then
        assertThat(account.getPrivileges()).hasSize(1);
        assertThat(account.getPrivileges().get(0).getName()).isEqualTo("OWN_PROFILE_MANAGEMENT");
    }

    @Test
    void givenCustomerAccountWhenAddNewAccountPrivilegesThenCreateSaveAndAdd() {
        //given
        final Account account = AccountDataGenerator.companyAccount(1);
        account.setAccountType(AccountType.CUSTOMER);

        when(privilegeRepository.save(any())).thenAnswer(invocation -> invocation.getArguments()[0]);

        //when
        privilegeService.addNewAccountPrivileges(account);

        //then
        assertThat(account.getPrivileges()).hasSize(1);
        assertThat(account.getPrivileges().get(0).getName()).isEqualTo("CREATE_CUSTOMER");
    }

    @Test
    void givenMainProfileWhenAddNewAccountProfilePrivilegesThenCreateSaveAndAdd() {
        //given
        final Account account = AccountDataGenerator.companyAccount(1);
        final AccountProfile accountProfile = AccountProfileDataGenerator.accountProfile(1, account);
        accountProfile.setProfileType(AccountProfileType.MAIN);

        when(privilegeRepository.save(any())).thenAnswer(invocation -> invocation.getArguments()[0]);

        //when
        privilegeService.addNewAccountProfilePrivileges(accountProfile);

        //then
        assertThat(accountProfile.getPrivileges()).hasSize(1);
        assertThat(accountProfile.getPrivileges().get(0).getName()).isEqualTo("COMPANY_FULL_MANAGEMENT");
    }

    @Test
    void givenSubProfileWhenAddNewProfileAccountPrivilegesThenCreateSaveAndAdd() {
        //given
        final Account account = AccountDataGenerator.companyAccount(1);
        final AccountProfile accountProfile = AccountProfileDataGenerator.accountProfile(1, account);
        accountProfile.setProfileType(AccountProfileType.SUBPROFILE);

        when(privilegeRepository.save(any())).thenAnswer(invocation -> invocation.getArguments()[0]);

        //when
        privilegeService.addNewAccountProfilePrivileges(accountProfile);

        //then
        assertThat(accountProfile.getPrivileges()).hasSize(1);
        assertThat(accountProfile.getPrivileges().get(0).getName()).isEqualTo("COMPANY_BASIC_MANAGEMENT");
    }

    @Test
    void givenAccountWhenAddCustomerCreatedPrivilegeThenAdd() {
        //given
        final Account account = Account.builder().build();

        final Privilege privilege = Privilege.builder().build();
        when(privilegeRepository.save(any())).thenReturn(privilege);

        //when
        privilegeService.addCustomerCreatedPrivilege(account);

        //then
        assertThat(account.getPrivileges()).hasSize(1);
        assertThat(account.getPrivileges().get(0)).isEqualTo(privilege);
    }

}