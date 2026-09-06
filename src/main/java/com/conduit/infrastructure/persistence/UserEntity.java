package com.conduit.infrastructure.persistence;

import com.conduit.domain.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    private UUID id;
    @Column(nullable = false, unique = true, length = 320)
    private String email;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(name = "password_hash", nullable = false, length = 500)
    private String passwordHash;
    private String bio;
    @Column(length = 2048)
    private String image;

    protected UserEntity() { }

    private UserEntity(UUID id, String email, String username, String passwordHash, String bio, String image) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.passwordHash = passwordHash;
        this.bio = bio;
        this.image = image;
    }

    static UserEntity fromDomain(User user) {
        return new UserEntity(user.id(), user.email(), user.username(), user.passwordHash(), user.bio(), user.image());
    }

    User toDomain() {
        return new User(id, email, username, passwordHash, bio, image);
    }

    UUID getId() { return id; }
    String getEmail() { return email; }
    String getUsername() { return username; }
}