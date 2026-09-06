package com.conduit.application.user;

import com.conduit.infrastructure.security.Argon2PasswordHasher;
import com.conduit.test.InMemoryUserRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegisterUserUseCaseTest {
    @Test
    void hashesPasswordAndRejectsShortPassword() {
        InMemoryUserRepository repository = new InMemoryUserRepository();
        var user = new RegisterUserUseCase(repository, new Argon2PasswordHasher())
                .execute("a@example.invalid", "alice", "password123");
        assertNotEquals("password123", user.passwordHash());
        UserException exception = assertThrows(UserException.class, () ->
                new RegisterUserUseCase(repository, new Argon2PasswordHasher())
                        .execute("b@example.invalid", "bob", "short"));
        assertEquals(UserException.ErrorKind.VALIDATION, exception.kind());
    }
}
