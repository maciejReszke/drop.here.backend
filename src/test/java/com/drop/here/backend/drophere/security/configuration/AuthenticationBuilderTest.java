package com.drop.here.backend.drophere.security.configuration;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.entity.Privilege;
import com.drop.here.backend.drophere.test_data.AccountDataGenerator;
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
        final Account account = AccountDataGenerator.companyAccount(1);
        account.setPrivileges(List.of(Privilege.builder().name("priv1").build()));
        final LocalDateTime time = LocalDateTime.now().minusMinutes(100);
        final PreAuthentication preAuthentication = new PreAuthentication("mail", time);

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
    }

}