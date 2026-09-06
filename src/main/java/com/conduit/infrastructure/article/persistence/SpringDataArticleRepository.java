package com.conduit.infrastructure.article.persistence;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataArticleRepository extends JpaRepository<ArticleEntity, UUID> {
  Optional<ArticleEntity> findBySlug(String slug);
}
