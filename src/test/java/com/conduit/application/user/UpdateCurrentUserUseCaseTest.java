package com.conduit.application.user;

import com.conduit.infrastructure.security.Argon2PasswordHasher;
import com.conduit.test.InMemoryUserRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UpdateCurrentUserUseCaseTest {
    @Test
    void updatesNullableFieldsAndRehashesPassword() {
        InMemoryUserRepository repository = new InMemoryUserRepository();
        var hasher = new Argon2PasswordHasher();
        var user = new RegisterUserUseCase(repository, hasher)
                .execute("a@example.invalid", "alice", "password123");
        var updated = new UpdateCurrentUserUseCase(repository, hasher).execute(user.id(),
                new UpdateCurrentUserUseCase.UpdateCommand(false, null, false, null,
                        true, "newpassword", true, "Hello", true, null));
        assertEquals("Hello", updated.bio());
        assertEquals(null, updated.image());
        assertNotEquals(user.passwordHash(), updated.passwordHash());
        assertTrue(hasher.matches("newpassword", updated.passwordHash()));
    }
}
