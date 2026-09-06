package com.conduit.infrastructure.user.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.conduit.domain.user.User;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EnabledIf(
    value = "isDockerAvailable",
    disabledReason = "Docker daemon is unavailable; H2 tests remain active")
class UserRepositoryPostgresTest {
  @SuppressWarnings("resource")
  static final PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>("postgres:16-alpine")
          .withDatabaseName("conduit")
          .withUsername("conduit")
          .withPassword("test-only");

  @Autowired SpringDataUserRepository repository;

  @DynamicPropertySource
  static void configureDatabase(DynamicPropertyRegistry registry) {
    postgres.start();
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
    registry.add("spring.flyway.enabled", () -> true);
  }

  @AfterAll
  static void stopDatabase() {
    postgres.close();
  }

  static boolean isDockerAvailable() {
    try {
      Process process =
          new ProcessBuilder("docker", "info", "--format", "{{.ServerVersion}}")
              .redirectErrorStream(true)
              .start();
      boolean completed = process.waitFor(10, TimeUnit.SECONDS);
      if (!completed) {
        process.destroyForcibly();
        return false;
      }
      return process.exitValue() == 0;
    } catch (Exception exception) {
      return false;
    }
  }

  @Test
  @SuppressWarnings("null")
  void persistsAndLoadsUserThroughPostgresAndFlyway() {
    User user = User.create("postgres@example.invalid", "postgres-user", "hash", null, null);
    UserEntity saved = repository.save(UserEntity.fromDomain(user));
    var savedId = Objects.requireNonNull(saved.getId());

    assertEquals(user.id(), repository.findById(savedId).orElseThrow().toDomain().id());
    assertEquals(
        user.email(), repository.findByEmail(user.email()).orElseThrow().toDomain().email());
  }
}
