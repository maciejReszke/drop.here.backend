package com.drop.here.backend.drophere.swagger;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@EnableSwagger2
@Configuration
@SuppressWarnings({"Guava", "squid:S4738"})
public class SwaggerConfiguration {
    private static final List<Predicate<String>> REST_API_PREDICATE = List.of(PathSelectors.ant("/**"));

    private static final Predicate<String> ALWAYS_FALSE_PREDICATE = input -> false;

    private static final List<Predicate<String>> NOT_INCLUDE_IN_DOCS = List.of(
            Predicates.not(PathSelectors.ant("/error"))
    );

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(apiPaths())
                .build()
                .securitySchemes(List.of(apiKey()))
                .groupName("drop.here API")
                .useDefaultResponseMessages(false)
                .apiInfo(apiInfo());
    }

    private ApiKey apiKey() {
        return new ApiKey("AUTHORIZATION", "Authorization", "header");
    }

    private Predicate<String> apiPaths() {
        return Stream.of(NOT_INCLUDE_IN_DOCS, REST_API_PREDICATE)
                .flatMap(Collection::stream)
                .reduce(Predicates::and)
                .orElse(ALWAYS_FALSE_PREDICATE);
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("drop.here API")
                .description("***** ***")
                .version("1.0")
                .build();
    }

}
