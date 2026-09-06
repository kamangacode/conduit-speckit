package com.conduit.application.user;

import com.conduit.infrastructure.security.Argon2PasswordHasher;
import com.conduit.test.InMemoryUserRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetCurrentUserUseCaseTest {
    @Test
    void retrievesOnlyTheAuthenticatedUser() {
        InMemoryUserRepository repository = new InMemoryUserRepository();
        var user = new RegisterUserUseCase(repository, new Argon2PasswordHasher())
                .execute("a@example.invalid", "alice", "password123");
        assertEquals(user.id(), new GetCurrentUserUseCase(repository).execute(user.id()).id());
        assertThrows(UserException.class, () -> new GetCurrentUserUseCase(repository).execute(null));
    }
}
