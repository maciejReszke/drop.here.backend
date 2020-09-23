package com.drop.here.backend.drophere.test_config;

import com.mongodb.MongoClientSettings;
import com.mongodb.reactivestreams.client.MongoClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.autoconfigure.mongo.ReactiveMongoClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory;
import org.springframework.test.context.junit.jupiter.EnabledIf;
import org.testcontainers.containers.MongoDBContainer;

import javax.annotation.PostConstruct;
import java.util.stream.Collectors;

@Configuration
@Profile("integration-testing")
@EnabledIf(expression = "${integration-tests.enabled}", loadContext = true)
public class MongoTestConfiguration extends AbstractReactiveMongoConfiguration {
    private final MongoProperties properties = new MongoProperties();

    private final Environment environment;
    private final ObjectProvider<MongoClientSettingsBuilderCustomizer> builderCustomizers;
    private final ObjectProvider<MongoClientSettings> settings;

    private final String mongoImageName;

    public MongoTestConfiguration(Environment environment,
                                  ObjectProvider<MongoClientSettingsBuilderCustomizer> builderCustomizers,
                                  ObjectProvider<MongoClientSettings> settings,
                                  @Value("${mongo.image-name}") String mongoImageName) {
        this.environment = environment;
        this.builderCustomizers = builderCustomizers;
        this.settings = settings;
        this.mongoImageName = mongoImageName;
    }


    @PostConstruct
    public void create() {
        final MongoTestContainer mongoTestContainer = new MongoTestContainer(mongoImageName);
        properties.setUri(mongoTestContainer.getReplicaSetUrl());
    }

    @Override
    protected String getDatabaseName() {
        // It is not used anyway
        return null;
    }

    @Bean
    @Override
    public ReactiveMongoDatabaseFactory reactiveMongoDbFactory() {
        return new SimpleReactiveMongoDatabaseFactory(reactiveMongoClient(), properties.getMongoClientDatabase());
    }

    @Override
    public MongoClient reactiveMongoClient() {
        return new ReactiveMongoClientFactory(properties, environment, builderCustomizers.orderedStream().collect(Collectors.toList()))
                .createMongoClient(settings.getIfAvailable());
    }

    private static class MongoTestContainer extends MongoDBContainer {

        public MongoTestContainer(String imageName) {
            super(imageName);
            start();
        }
    }
}
