package com.conduit.interfaces.rest.shared;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.conduit.ConduitApplication;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

class RequiredConfigurationStartupTest {
  @Test
  void failsWhenDatabaseUrlIsBlank() {
    Throwable failure =
        startAndCapture(
            "DATABASE_URL=",
            "DATABASE_USERNAME=sa",
            "DATABASE_PASSWORD=",
            "JWT_SECRET=test-secret-test-secret-test-secret-test-secret",
            "spring.flyway.enabled=false",
            "spring.jpa.hibernate.ddl-auto=create-drop");

    assertContains(failure, "DATABASE_URL");
  }

  @Test
  void failsWhenJwtSecretIsBlank() {
    Throwable failure =
        startAndCapture(
            "DATABASE_URL=jdbc:h2:mem:missing-jwt",
            "DATABASE_USERNAME=sa",
            "DATABASE_PASSWORD=test-only",
            "JWT_SECRET=",
            "spring.flyway.enabled=false",
            "spring.jpa.hibernate.ddl-auto=create-drop");

    assertContains(failure, "conduit.jwt.secret");
  }

  private static Throwable startAndCapture(String... properties) {
    Map<String, String> previous = new HashMap<>();
    for (String property : properties) {
      int separator = property.indexOf('=');
      String key = property.substring(0, separator);
      previous.put(key, System.getProperty(key));
      System.setProperty(key, property.substring(separator + 1));
    }
    try (ConfigurableApplicationContext ignored =
        new SpringApplicationBuilder(ConduitApplication.class)
            .web(WebApplicationType.NONE)
            .properties(properties)
            .run()) {
      fail("Application startup should fail when required configuration is missing");
      return new AssertionError("unreachable");
    } catch (Throwable failure) {
      return failure;
    } finally {
      previous.forEach(
          (key, value) -> {
            if (value == null) {
              System.clearProperty(key);
            } else {
              System.setProperty(key, value);
            }
          });
    }
  }

  private static void assertContains(Throwable failure, String expected) {
    Throwable current = failure;
    while (current != null) {
      if (String.valueOf(current.getMessage()).contains(expected)) {
        assertTrue(true);
        return;
      }
      current = current.getCause();
    }
    fail("Expected startup failure to mention " + expected + ", but got: " + failure);
  }
}
