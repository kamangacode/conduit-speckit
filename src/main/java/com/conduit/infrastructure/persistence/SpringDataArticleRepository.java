package com.conduit.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataArticleRepository extends JpaRepository<ArticleEntity, UUID> {
    Optional<ArticleEntity> findBySlug(String slug);
}