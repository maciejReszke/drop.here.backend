package com.drop.here.backend.drophere.authentication.account.service;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.enums.AccountType;
import com.drop.here.backend.drophere.authentication.account.repository.PrivilegeRepository;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
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
        assertThat(account.getPrivileges().get(0).getName()).isEqualTo("CREATE_COMPANY");
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

}