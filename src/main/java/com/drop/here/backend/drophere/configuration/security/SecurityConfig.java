package com.drop.here.backend.drophere.configuration.security;

import com.drop.here.backend.drophere.authentication.account.service.PrivilegeService;
import com.drop.here.backend.drophere.configuration.logging.ReactiveSpringLoggingFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

@EnableWebFluxSecurity
@Configuration
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private static final ServerWebExchangeMatcher TOKEN_AUTHENTICATION_PATH_MATCHER = ServerWebExchangeMatchers.pathMatchers("/**");

    // TODO: 23/09/2020 sprawdzic swaggera
    @Bean
    protected SecurityWebFilterChain configure(ServerHttpSecurity http,
                                               JwtTokenAuthenticationExtractor jwtTokenAuthenticationExtractor,
                                               JwtTokenReactiveAuthenticationManager jwtTokenReactiveAuthenticationManager,
                                               ForbiddenServerEntryPoint forbiddenServerEntryPoint,
                                               @Value("${logging.request.logBody}") boolean logRequestBody,
                                               @Value("${logging.response.logBody}") boolean logResponseBody) {
        return http
                .authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec
                        .pathMatchers(HttpMethod.GET, "/favicon.ico").permitAll()
                        // TODO: 23/09/2020 anonymous
                        .pathMatchers(HttpMethod.POST, "/authentication").permitAll()
                        // TODO: 23/09/2020 anonymous
                        .pathMatchers(HttpMethod.POST, "/authentication/external").permitAll()
                        .pathMatchers(HttpMethod.POST, "/authentication/profile").hasAuthority(PrivilegeService.OWN_PROFILE_MANAGEMENT_PRIVILEGE)
                        .pathMatchers(HttpMethod.POST, "/accounts/profiles").hasAuthority(PrivilegeService.OWN_PROFILE_MANAGEMENT_PRIVILEGE)
                        .pathMatchers(HttpMethod.PATCH, "/accounts/profiles").hasAnyAuthority(PrivilegeService.COMPANY_FULL_MANAGEMENT_PRIVILEGE, PrivilegeService.COMPANY_BASIC_MANAGEMENT_PRIVILEGE)
                        .pathMatchers(HttpMethod.POST, "/accounts/profiles/images").hasAnyAuthority(PrivilegeService.COMPANY_FULL_MANAGEMENT_PRIVILEGE, PrivilegeService.COMPANY_BASIC_MANAGEMENT_PRIVILEGE)
                        .pathMatchers(HttpMethod.GET, "/accounts/profiles/{profileUid}/images").permitAll()
                        // TODO: 23/09/2020 anonymous
                        .pathMatchers(HttpMethod.POST, "/accounts").permitAll()
                        .pathMatchers(HttpMethod.GET, "/accounts").authenticated()
                        .pathMatchers(HttpMethod.GET, "/authentication").authenticated()
                        .pathMatchers(HttpMethod.GET, "/companies/{companyUid}/categories").authenticated()
                        .pathMatchers(HttpMethod.GET, "/units").authenticated()
                        .pathMatchers(HttpMethod.GET, "/countries").authenticated()
                        .pathMatchers(HttpMethod.GET, "/companies/{companyUid}/products").authenticated()
                        .pathMatchers(HttpMethod.POST, "/companies/{companyUid}/products").hasAuthority(PrivilegeService.COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE)
                        .pathMatchers("/companies/{companyUid}/products/{productId}").hasAuthority(PrivilegeService.COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE)
                        .pathMatchers(HttpMethod.POST, "/companies/{companyUid}/products/{productId}/images").hasAuthority(PrivilegeService.COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE)
                        .pathMatchers(HttpMethod.GET, "/companies/{companyUid}/products/{productId}/images").permitAll()
                        .pathMatchers("/companies/{companyUid}/products/{productId}/customizations/**").hasAuthority(PrivilegeService.COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE)
                        .pathMatchers("/companies/{companyUid}/spots/**").hasAuthority(PrivilegeService.COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE)
                        .pathMatchers(HttpMethod.PUT, "/management/companies").hasAuthority(PrivilegeService.COMPANY_FULL_MANAGEMENT_PRIVILEGE)
                        .pathMatchers(HttpMethod.POST, "/management/companies/images").hasAuthority(PrivilegeService.COMPANY_FULL_MANAGEMENT_PRIVILEGE)
                        .pathMatchers(HttpMethod.GET, "/management/companies").hasAnyAuthority(PrivilegeService.COMPANY_FULL_MANAGEMENT_PRIVILEGE, PrivilegeService.COMPANY_BASIC_MANAGEMENT_PRIVILEGE)
                        .pathMatchers("/management/companies/customers/**").hasAuthority(PrivilegeService.COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE)
                        .pathMatchers(HttpMethod.GET, "/companies/{companyUid}/images").permitAll()
                        .pathMatchers(HttpMethod.PUT, "/management/customers").hasAuthority(PrivilegeService.NEW_ACCOUNT_CREATE_CUSTOMER_PRIVILEGE)
                        .pathMatchers(HttpMethod.GET, "/management/customers").hasAnyAuthority(PrivilegeService.NEW_ACCOUNT_CREATE_CUSTOMER_PRIVILEGE, PrivilegeService.CUSTOMER_CREATED_PRIVILEGE)
                        .pathMatchers(HttpMethod.POST, "/management/customers/images").hasAuthority(PrivilegeService.CUSTOMER_CREATED_PRIVILEGE)
                        .pathMatchers(HttpMethod.GET, "/customers/{customerId}/images").permitAll()
                        .pathMatchers("/spots/**").hasAuthority(PrivilegeService.CUSTOMER_CREATED_PRIVILEGE)
                        .pathMatchers("/actuator/**").permitAll()
                        .pathMatchers("/notifications/**").hasAnyAuthority(PrivilegeService.CUSTOMER_CREATED_PRIVILEGE, PrivilegeService.COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE)
                        .pathMatchers("/companies/{companyUid}/schedule_templates/**").hasAuthority(PrivilegeService.COMPANY_RESOURCES_MANAGEMENT_PRIVILEGE)
                        .pathMatchers("/webjars/springfox-swagger-ui/**").permitAll()
                        .pathMatchers("/v2/**").permitAll()
                        .pathMatchers("/swagger-ui.html/**").permitAll()
                        .pathMatchers("/swagger-resources/**").permitAll()
                        .pathMatchers("/css/**").permitAll()
                        .anyExchange().denyAll())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .logout(ServerHttpSecurity.LogoutSpec::disable)
                .addFilterBefore(reactiveSpringLoggingFilter(logRequestBody, logResponseBody), SecurityWebFiltersOrder.AUTHENTICATION)
                .addFilterAt(jwtTokenAuthenticationFilter(jwtTokenAuthenticationExtractor, jwtTokenReactiveAuthenticationManager, forbiddenServerEntryPoint), SecurityWebFiltersOrder.AUTHENTICATION)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .exceptionHandling(exceptionHandlingSpec -> exceptionHandlingSpec.authenticationEntryPoint(forbiddenServerEntryPoint))
                .build();
    }

    private AuthenticationWebFilter jwtTokenAuthenticationFilter(JwtTokenAuthenticationExtractor jwtTokenAuthenticationExtractor,
                                                                 JwtTokenReactiveAuthenticationManager jwtTokenReactiveAuthenticationManager,
                                                                 ForbiddenServerEntryPoint forbiddenServerEntryPoint) {
        return new JwtTokenAuthenticationWebFilter(
                jwtTokenReactiveAuthenticationManager,
                jwtTokenAuthenticationExtractor,
                TOKEN_AUTHENTICATION_PATH_MATCHER,
                forbiddenServerEntryPoint
        );
    }

    private ReactiveSpringLoggingFilter reactiveSpringLoggingFilter(boolean logRequestBody, boolean logResponseBody) {
        return new ReactiveSpringLoggingFilter(logRequestBody, logResponseBody);
    }
}
