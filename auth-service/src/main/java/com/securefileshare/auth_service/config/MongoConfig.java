package com.securefileshare.auth_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.securefileshare.auth_service.repository")
@EnableMongoAuditing
public class MongoConfig {
} 