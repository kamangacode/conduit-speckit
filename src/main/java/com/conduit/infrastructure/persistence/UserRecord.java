package com.conduit.infrastructure.persistence;

import com.conduit.domain.user.User;

import java.util.UUID;

public record UserRecord(UUID id, String email, String username, String passwordHash, String bio, String image) {
    User toDomain() {
        return new User(id, email, username, passwordHash, bio, image);
    }

    static UserRecord fromDomain(User user) {
        return new UserRecord(user.id(), user.email(), user.username(), user.passwordHash(), user.bio(), user.image());
    }
}
