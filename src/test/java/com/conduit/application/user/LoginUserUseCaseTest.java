package com.conduit.application.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.conduit.infrastructure.user.security.Argon2PasswordHasher;
import com.conduit.test.InMemoryUserRepository;
import org.junit.jupiter.api.Test;

class LoginUserUseCaseTest {
  @Test
  void verifiesPasswordAndUsesGenericInvalidCredentials() {
    InMemoryUserRepository repository = new InMemoryUserRepository();
    var hasher = new Argon2PasswordHasher();
    new RegisterUserUseCase(repository, hasher)
        .execute("a@example.invalid", "alice", "password123");
    assertEquals(
        "alice",
        new LoginUserUseCase(repository, hasher)
            .execute("a@example.invalid", "password123")
            .username());
    UserException exception =
        assertThrows(
            UserException.class,
            () ->
                new LoginUserUseCase(repository, hasher)
                    .execute("a@example.invalid", "wrongpassword"));
    assertEquals("credentials", exception.field());
  }
}
