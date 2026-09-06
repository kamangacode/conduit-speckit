package com.conduit.interfaces.rest.article;

import com.conduit.application.user.UserRepository;
import com.conduit.domain.article.Article;
import com.conduit.domain.user.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.List;

public final class ArticleResponseMapper {
  private ArticleResponseMapper() {}

  public static ArticleResponse toResponse(Article article, UserRepository users) {
    return new ArticleResponse(toPayload(article, users, true));
  }

  public static ArticleListResponse toListResponse(
      List<Article> articles, long total, UserRepository users) {
    return new ArticleListResponse(
        articles.stream().map(article -> toPayload(article, users, false)).toList(), total);
  }

  private static Payload toPayload(Article article, UserRepository users, boolean includeBody) {
    User author = users.findById(article.authorId()).orElseThrow();
    return new Payload(
        article.slug(),
        article.title(),
        article.description(),
        includeBody ? article.body() : null,
        article.tagValues(),
        article.createdAt(),
        article.updatedAt(),
        new Author(author.username(), author.bio(), author.image(), false),
        false,
        0);
  }

  public record ArticleResponse(Payload article) {}

  public record ArticleListResponse(List<Payload> articles, long articlesCount) {}

  public record Payload(
      String slug,
      String title,
      String description,
      @JsonInclude(JsonInclude.Include.NON_NULL) String body,
      List<String> tagList,
      Instant createdAt,
      Instant updatedAt,
      Author author,
      boolean favorited,
      int favoritesCount) {}

  public record Author(String username, String bio, String image, boolean following) {}
}
