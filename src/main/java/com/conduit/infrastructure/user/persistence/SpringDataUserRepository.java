package com.conduit.infrastructure.user.persistence;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataUserRepository extends JpaRepository<UserEntity, UUID> {
  Optional<UserEntity> findByEmail(String email);

  Optional<UserEntity> findByUsername(String username);

  boolean existsByEmail(String email);

  boolean existsByEmailAndIdNot(String email, UUID id);

  boolean existsByUsername(String username);

  boolean existsByUsernameAndIdNot(String username, UUID id);
}
