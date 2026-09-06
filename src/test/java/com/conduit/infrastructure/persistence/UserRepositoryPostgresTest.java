package com.conduit.infrastructure.persistence;

import com.conduit.domain.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers(disabledWithoutDocker = true)
class UserRepositoryPostgresTest {
    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("conduit")
            .withUsername("conduit")
            .withPassword("test-only");

    @Autowired SpringDataUserRepository repository;

    @DynamicPropertySource
    static void configureDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> true);
    }

    @Test
    void persistsAndLoadsUserThroughPostgresAndFlyway() {
        User user = User.create("postgres@example.invalid", "postgres-user", "hash", null, null);
        UserEntity saved = repository.save(UserEntity.fromDomain(user));

        assertEquals(user.id(), repository.findById(saved.getId()).orElseThrow().toDomain().id());
        assertEquals(user.email(), repository.findByEmail(user.email()).orElseThrow().toDomain().email());
    }
}