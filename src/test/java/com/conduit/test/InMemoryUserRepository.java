package com.conduit.test;

import com.conduit.application.user.UserRepository;
import com.conduit.domain.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class InMemoryUserRepository implements UserRepository {
    private final List<User> users = new ArrayList<>();

    @Override
    public User save(User user) {
        users.removeIf(existing -> existing.id().equals(user.id()));
        users.add(user);
        return user;
    }

    @Override public Optional<User> findById(UUID id) { return users.stream().filter(user -> user.id().equals(id)).findFirst(); }
    @Override public Optional<User> findByEmail(String email) { return users.stream().filter(user -> user.email().equals(email)).findFirst(); }
    @Override public boolean existsByEmail(String email, UUID excludedId) { return users.stream().anyMatch(user -> user.email().equals(email) && !user.id().equals(excludedId)); }
    @Override public boolean existsByUsername(String username, UUID excludedId) { return users.stream().anyMatch(user -> user.username().equals(username) && !user.id().equals(excludedId)); }
}
