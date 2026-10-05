package com.energyplatform.auth_service.config;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

@Configuration
@EnableConfigurationProperties(MongoConfigProperties.class)
@EnableReactiveMongoAuditing
public class MongoConfig {

    private final MongoConfigProperties properties;

    public MongoConfig(MongoConfigProperties properties) {
        this.properties = properties;
    }

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create(properties.getUri());
    }

    @Bean
    public ReactiveMongoTemplate reactiveMongoTemplate(@Qualifier("mongoClient") MongoClient mongoClient) {
        return new ReactiveMongoTemplate(mongoClient, properties.getDatabases().get("metadata"));
    }
}
