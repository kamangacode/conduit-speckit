package com.conduit.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class RequiredConfiguration {
    public RequiredConfiguration(
            @Value("${DATABASE_URL}") String databaseUrl,
            @Value("${DATABASE_USERNAME}") String databaseUsername,
            @Value("${DATABASE_PASSWORD}") String databasePassword) {
        requireValue(databaseUrl, "DATABASE_URL");
        requireValue(databaseUsername, "DATABASE_USERNAME");
        requireValue(databasePassword, "DATABASE_PASSWORD");
    }

    private static void requireValue(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(name + " must be configured");
        }
    }
}
