package com.conduit.application.user;

import com.conduit.domain.user.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email, UUID excludedId);
    boolean existsByUsername(String username, UUID excludedId);
}
