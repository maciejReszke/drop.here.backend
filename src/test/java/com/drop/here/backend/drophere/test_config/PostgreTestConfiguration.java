package com.drop.here.backend.drophere.test_config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.junit.jupiter.EnabledIf;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;

@Configuration
@Profile("integration-testing")
@EnabledIf(expression = "${integration-tests.enabled}", loadContext = true)
public class PostgreTestConfiguration {
    private final PostgreSQLContainer<?> container;

    public PostgreTestConfiguration(@Value("${test-containers.postgresql.image}") String image,
                                    @Value("${test-containers.postgresql.port}") int port) {
        container = new PostgreSQLContainer<>(image)
                .withExposedPorts(port);
        container.start();
    }

    @Bean
    public DataSource dataSource() {
        return DataSourceBuilder.create()
                .driverClassName(container.getDriverClassName())
                .password(container.getPassword())
                .url(container.getJdbcUrl())
                .username(container.getUsername())
                .build();
    }
}
