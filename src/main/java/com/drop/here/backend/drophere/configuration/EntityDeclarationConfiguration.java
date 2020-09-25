package com.drop.here.backend.drophere.configuration;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.company.entity.Company;
import com.drop.here.backend.drophere.company.entity.CompanyCustomerRelationship;
import com.drop.here.backend.drophere.country.Country;
import com.drop.here.backend.drophere.customer.entity.Customer;
import com.drop.here.backend.drophere.product.entity.Product;
import com.drop.here.backend.drophere.product.entity.ProductUnit;
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
                .then(createCollection(reactiveMongoTemplate, Country.class))
                .then(createCollection(reactiveMongoTemplate, ProductUnit.class))
                .then(createCollection(reactiveMongoTemplate, Product.class))
                .then(createCollection(reactiveMongoTemplate, CompanyCustomerRelationship.class))
                .then(createCollection(reactiveMongoTemplate, Customer.class))
                .then(createCollection(reactiveMongoTemplate, Company.class))
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
