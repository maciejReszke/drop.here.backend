package com.drop.here.backend.drophere.configuration;

import com.mongodb.reactivestreams.client.MongoClient;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.mongo.reactivestreams.ReactiveStreamsMongoLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ConditionalOnProperty(value = "schedule.enabled", havingValue = "true")
@EnableSchedulerLock(defaultLockAtMostFor = "PT30S")
public class SchedulingConfig {

    @Bean
    public LockProvider lockProvider(MongoClient mongoClient, @Value("${shedlock.database.name}") String databaseName) {
        return new ReactiveStreamsMongoLockProvider(mongoClient.getDatabase(databaseName));
    }
}