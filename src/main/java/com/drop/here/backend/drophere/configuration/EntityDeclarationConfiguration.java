package com.drop.here.backend.drophere.configuration;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.mongodb.reactivestreams.client.MongoCollection;
import org.bson.Document;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Mono;

@Configuration
public class EntityDeclarationConfiguration {

    // TODO: 24/09/2020 !!!
    @Bean(name = "declaredCollections")
    public MongoCollection<Document> declareCollections(ReactiveMongoTemplate reactiveMongoTemplate) {
        return createCollection(reactiveMongoTemplate, Account.class)
                .block();

    }

    private Mono<MongoCollection<Document>> createCollection(ReactiveMongoTemplate reactiveMongoTemplate, Class<?> clazz) {
        return reactiveMongoTemplate.createCollection(toCamelCase(clazz))
                .onErrorResume(ignore -> Mono.empty());
    }

    private String toCamelCase(Class<?> clazz) {
        return clazz.getSimpleName().substring(0, 1).toLowerCase() + clazz.getSimpleName().substring(1);
    }
}
