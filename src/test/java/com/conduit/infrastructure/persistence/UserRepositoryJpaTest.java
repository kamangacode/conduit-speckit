package com.conduit.infrastructure.persistence;

import com.conduit.domain.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class UserRepositoryJpaTest {
    @Autowired SpringDataUserRepository repository;

    @Test
    void persistsAndLoadsUserThroughHibernate() {
        User user = User.create("jpa@example.invalid", "jpa-user", "hash", null, null);
        UserEntity saved = repository.save(UserEntity.fromDomain(user));

        assertEquals(user.id(), repository.findById(saved.getId()).orElseThrow().toDomain().id());
        assertEquals(user.email(), repository.findByEmail(user.email()).orElseThrow().toDomain().email());
    }
}