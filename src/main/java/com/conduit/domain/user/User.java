package com.conduit.domain.user;

import java.util.UUID;

public record User(
        UUID id,
        String email,
        String username,
        String passwordHash,
        String bio,
        String image) {

    public User {
        if (id == null) {
            throw new IllegalArgumentException("id is required");
        }
        requireNonBlank(email, "email");
        requireNonBlank(username, "username");
        requireNonBlank(passwordHash, "passwordHash");
    }

    public static User create(String email, String username, String passwordHash, String bio, String image) {
        return new User(UUID.randomUUID(), email, username, passwordHash, bio, image);
    }

    public User update(String email, String username, String passwordHash, String bio, String image) {
        return new User(id,
                email == null ? this.email : email,
                username == null ? this.username : username,
                passwordHash == null ? this.passwordHash : passwordHash,
                bio,
                image);
    }

    private static void requireNonBlank(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
    }
}
