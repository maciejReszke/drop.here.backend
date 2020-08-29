package com.drop.here.backend.drophere.security.configuration;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.AccountProfile;
import com.drop.here.backend.drophere.authentication.account.entity.Privilege;
import com.drop.here.backend.drophere.company.Company;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
import com.drop.here.backend.drophere.test_data.AccountProfileDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AuthenticationBuilderTest {
    @InjectMocks
    private AuthenticationBuilder authenticationBuilder;

    @Test
    void givenAccountWhenBuildAuthenticationThenBuild() {
        //given
        final Company company = Company.builder().build();
        final Account account = AccountDataGenerator.companyAccount(1, company);
        account.setPrivileges(List.of(Privilege.builder().name("priv1").build()));
        final Customer customer = Customer.builder().build();
        account.setCustomer(customer);
        final LocalDateTime time = LocalDateTime.now().minusMinutes(100);
        final PreAuthentication preAuthentication = PreAuthentication.withoutProfile("mail", time);

        //when
        final AccountAuthentication result = authenticationBuilder.buildAuthentication(account, preAuthentication);

        //then
        assertThat(result.getAuthorities()).hasSize(1);
        assertThat(((SimpleGrantedAuthority) (result.getAuthorities().toArray()[0])).getAuthority()).isEqualTo("priv1");
        assertThat(result.getCredentials()).isNull();
        assertThat(result.getName()).isEqualTo(account.getMail());
        assertThat(result.getDetails()).isEqualTo(account);
        assertThat(result.getPrincipal()).isEqualTo(account);
        assertThat(result.getTokenValidUntil()).isEqualTo(time);
        assertThat(result.getCompany()).isEqualTo(account.getCompany());
        assertThat(result.getCustomer()).isEqualTo(account.getCustomer());
    }

    @Test
    void givenAccountAndProfileWhenBuildAuthenticationThenBuild() {
        //given
        final Company company = Company.builder().build();
        final Account account = AccountDataGenerator.companyAccount(1, company);
        account.setPrivileges(List.of(Privilege.builder().name("priv1").build()));
        final LocalDateTime time = LocalDateTime.now().minusMinutes(100);
        final PreAuthentication preAuthentication = PreAuthentication.withoutProfile("mail", time);
        final AccountProfile accountProfile = AccountProfileDataGenerator.accountProfile(1, account);
        accountProfile.setPrivileges(List.of(Privilege.builder().name("priv2").build()));
        //when
        final AccountAuthentication result = authenticationBuilder.buildAuthentication(account, accountProfile, preAuthentication);

        //then
        assertThat(result.getAuthorities()).hasSize(2);
        assertThat(((SimpleGrantedAuthority) (result.getAuthorities().toArray()[0])).getAuthority()).isEqualTo("priv1");
        assertThat(((SimpleGrantedAuthority) (result.getAuthorities().toArray()[1])).getAuthority()).isEqualTo("priv2");
        assertThat(result.getCredentials()).isNull();
        assertThat(result.getName()).isEqualTo(account.getMail());
        assertThat(result.getDetails()).isEqualTo(account);
        assertThat(result.getPrincipal()).isEqualTo(account);
        assertThat(result.getTokenValidUntil()).isEqualTo(time);
        assertThat(result.getCompany()).isEqualTo(account.getCompany());
        assertThat(result.getCustomer()).isEqualTo(account.getCustomer());
    }

}