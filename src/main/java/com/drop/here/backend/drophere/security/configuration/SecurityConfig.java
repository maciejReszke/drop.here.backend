package com.drop.here.backend.drophere.security.configuration;

import com.drop.here.backend.drophere.authentication.account.service.PrivilegeService;
import com.drop.here.backend.drophere.authentication.token.JwtService;
import com.drop.here.backend.drophere.location.endpoint.LocationWebSocketController;
import com.drop.here.backend.drophere.security.configuration.websocket.WebSocketConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final JwtService jwtService;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .addFilter(new JwtAuthenticationFilter(authenticationManager(), jwtService))
                .authorizeRequests(registry -> registry
                        .mvcMatchers(HttpMethod.GET, "/favicon.ico").permitAll()
                        .mvcMatchers(HttpMethod.POST, "/authentication").anonymous()
                        .mvcMatchers(HttpMethod.POST, "/authentication/external").anonymous()
                        .mvcMatchers(HttpMethod.POST, "/authentication/profile").hasAuthority(PrivilegeService.OWN_PROFILE_MANAGEMENT_PRIVILEGE)
                        .mvcMatchers(HttpMethod.DELETE, "/authentication/profile").hasAnyAuthority(PrivilegeService.LOGGED_ON_ANY_PROFILE_COMPANY)
                        .mvcMatchers(HttpMethod.POST, "/accounts/profiles").hasAuthority(PrivilegeService.OWN_PROFILE_MANAGEMENT_PRIVILEGE)
                        .mvcMatchers(HttpMethod.PATCH, "/accounts/profiles").hasAnyAuthority(PrivilegeService.LOGGED_ON_ANY_PROFILE_COMPANY)
                        .mvcMatchers(HttpMethod.POST, "/accounts/profiles/images").hasAnyAuthority(PrivilegeService.LOGGED_ON_ANY_PROFILE_COMPANY)
                        .mvcMatchers(HttpMethod.GET, "/accounts/profiles/{profileUid}/images").permitAll()
                        .mvcMatchers(HttpMethod.POST, "/accounts").anonymous()
                        .mvcMatchers(HttpMethod.GET, "/accounts").authenticated()
                        .mvcMatchers(HttpMethod.GET, "/authentication").authenticated()
                        .mvcMatchers(HttpMethod.GET, "/companies/{companyUid}/categories").authenticated()
                        .mvcMatchers(HttpMethod.GET, "/units").authenticated()
                        .mvcMatchers(HttpMethod.GET, "/countries").authenticated()
                        .mvcMatchers(HttpMethod.GET, "/companies/{companyUid}/products").authenticated()
                        .mvcMatchers(HttpMethod.POST, "/companies/{companyUid}/products").hasAuthority(PrivilegeService.COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE)
                        .mvcMatchers("/companies/{companyUid}/products/{productId}").hasAuthority(PrivilegeService.COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE)
                        .mvcMatchers(HttpMethod.POST, "/companies/{companyUid}/products/{productId}/images").hasAuthority(PrivilegeService.COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE)
                        .mvcMatchers(HttpMethod.GET, "/companies/{companyUid}/products/{productId}/images").permitAll()
                        .mvcMatchers("/companies/{companyUid}/spots/**").hasAuthority(PrivilegeService.COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE)
                        .mvcMatchers(HttpMethod.PUT, "/management/companies").hasAuthority(PrivilegeService.COMPANY_FULL_MANAGEMENT_PRIVILEGE)
                        .mvcMatchers(HttpMethod.POST, "/management/companies/images").hasAuthority(PrivilegeService.COMPANY_FULL_MANAGEMENT_PRIVILEGE)
                        .mvcMatchers("/management/companies/drops/**").hasAuthority(PrivilegeService.COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE)
                        .mvcMatchers(HttpMethod.GET, "/management/companies").hasAnyAuthority(PrivilegeService.LOGGED_ON_ANY_PROFILE_COMPANY)
                        .mvcMatchers("/management/companies/customers/**").hasAuthority(PrivilegeService.COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE)
                        .mvcMatchers(HttpMethod.GET, "/companies/{companyUid}/images").permitAll()
                        .mvcMatchers(HttpMethod.PUT, "/management/customers").hasAuthority(PrivilegeService.NEW_ACCOUNT_CREATE_CUSTOMER_PRIVILEGE)
                        .mvcMatchers(HttpMethod.GET, "/management/customers").hasAnyAuthority(PrivilegeService.NEW_ACCOUNT_CREATE_CUSTOMER_PRIVILEGE, PrivilegeService.CUSTOMER_CREATED_PRIVILEGE)
                        .mvcMatchers(HttpMethod.POST, "/management/customers/images").hasAuthority(PrivilegeService.CUSTOMER_CREATED_PRIVILEGE)
                        .mvcMatchers(HttpMethod.GET, "/customers/{customerId}/images").permitAll()
                        .mvcMatchers("/spots/**").hasAuthority(PrivilegeService.CUSTOMER_CREATED_PRIVILEGE)
                        .mvcMatchers("/drops/**").hasAuthority(PrivilegeService.CUSTOMER_CREATED_PRIVILEGE)
                        .mvcMatchers("/actuator/**").permitAll()
                        .mvcMatchers("/notifications/**").hasAnyAuthority(PrivilegeService.CUSTOMER_CREATED_PRIVILEGE, PrivilegeService.COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE)
                        .mvcMatchers("/companies/{companyUid}/routes/**").hasAuthority(PrivilegeService.COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE)
                        .mvcMatchers(LocationWebSocketController.ENDPOINT + "/**").permitAll()
                        .mvcMatchers(WebSocketConfig.WEB_SOCKET_DESTINATION_PREFIX + "/**").permitAll()
                        .mvcMatchers("/companies/{companyUid}/drops/{dropUid}/shipments/**").hasAuthority(PrivilegeService.CUSTOMER_CREATED_PRIVILEGE)
                        .mvcMatchers(HttpMethod.GET, "/shipments").hasAuthority(PrivilegeService.CUSTOMER_CREATED_PRIVILEGE)
                        .mvcMatchers(HttpMethod.GET, "/shipments/{shipmentId}").hasAuthority(PrivilegeService.CUSTOMER_CREATED_PRIVILEGE)
                        .mvcMatchers("/companies/{companyUid}/shipments/**").hasAuthority(PrivilegeService.COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE)
                        .anyRequest().denyAll())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptions -> exceptions.defaultAuthenticationEntryPointFor(new Http401UnauthorizedEntryPoint(), new AntPathRequestMatcher("/**")));
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(jwtAuthenticationProvider);
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
                .mvcMatchers("/webjars/springfox-swagger-ui/**")
                .mvcMatchers("/v2/**")
                .mvcMatchers("/swagger-ui.html/**")
                .mvcMatchers("/swagger-resources/**")
                .mvcMatchers("/css/**");
    }
}
