package com.conduit.infrastructure.article.persistence;

import com.conduit.domain.article.Article;
import com.conduit.domain.article.Tag;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "articles")
public class ArticleEntity {
  @Id private UUID id;

  @Column(name = "author_id", nullable = false)
  private UUID authorId;

  @Column(nullable = false, unique = true)
  private String slug;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false, length = 1000)
  private String description;

  @Column(nullable = false, columnDefinition = "text")
  private String body;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "article_tags", joinColumns = @JoinColumn(name = "article_id"))
  @Column(name = "tag", nullable = false)
  private Set<String> tags = new LinkedHashSet<>();

  protected ArticleEntity() {}

  private ArticleEntity(Article article) {
    id = article.id();
    authorId = article.authorId();
    slug = article.slug();
    title = article.title();
    description = article.description();
    body = article.body();
    createdAt = article.createdAt();
    updatedAt = article.updatedAt();
    tags = new LinkedHashSet<>(article.tags().stream().map(Tag::value).toList());
  }

  static ArticleEntity fromDomain(Article article) {
    return new ArticleEntity(article);
  }

  Article toDomain() {
    return new Article(
        id,
        authorId,
        slug,
        title,
        description,
        body,
        tags.stream().map(Tag::new).toList(),
        createdAt,
        updatedAt);
  }

  UUID getId() {
    return id;
  }

  UUID getAuthorId() {
    return authorId;
  }

  String getSlug() {
    return slug;
  }

  String getTitle() {
    return title;
  }

  Set<String> getTags() {
    return tags;
  }

  Instant getCreatedAt() {
    return createdAt;
  }
}
