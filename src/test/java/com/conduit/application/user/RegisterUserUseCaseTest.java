package com.conduit.application.user;

import static org.junit.jupiter.api.Assertions.*;

import com.conduit.infrastructure.user.security.Argon2PasswordHasher;
import com.conduit.test.InMemoryUserRepository;
import org.junit.jupiter.api.Test;

class RegisterUserUseCaseTest {
  @Test
  void hashesPasswordAndRejectsShortPassword() {
    InMemoryUserRepository repository = new InMemoryUserRepository();
    var user =
        new RegisterUserUseCase(repository, new Argon2PasswordHasher())
            .execute("a@example.invalid", "alice", "password123");
    assertNotEquals("password123", user.passwordHash());
    UserException exception =
        assertThrows(
            UserException.class,
            () ->
                new RegisterUserUseCase(repository, new Argon2PasswordHasher())
                    .execute("b@example.invalid", "bob", "short"));
    assertEquals(UserException.ErrorKind.VALIDATION, exception.kind());
  }
}
