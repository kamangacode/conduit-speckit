package com.conduit.infrastructure.persistence;

import com.conduit.application.user.UserRepository;
import com.conduit.domain.user.User;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepositoryAdapter implements UserRepository {
    private final SpringDataUserRepository repository;

    public UserRepositoryAdapter(SpringDataUserRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public User save(User user) {
        return repository.save(UserEntity.fromDomain(user)).toDomain();
    }

    @Override
    public Optional<User> findById(UUID id) {
        return repository.findById(id).map(UserEntity::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email).map(UserEntity::toDomain);
    }

    @Override
    public boolean existsByEmail(String email, UUID excludedId) {
        return excludedId == null ? repository.existsByEmail(email) : repository.existsByEmailAndIdNot(email, excludedId);
    }

    @Override
    public boolean existsByUsername(String username, UUID excludedId) {
        return excludedId == null ? repository.existsByUsername(username) : repository.existsByUsernameAndIdNot(username, excludedId);
    }
}
