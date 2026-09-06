package com.conduit.interfaces.rest;

import com.conduit.application.article.ArticleCommands;
import com.conduit.application.article.ArticleException;
import com.conduit.application.article.ArticleRepository;
import com.conduit.application.article.ArticleUseCaseConfiguration.CreateArticleUseCase;
import com.conduit.application.article.ArticleUseCaseConfiguration.DeleteArticleUseCase;
import com.conduit.application.article.ArticleUseCaseConfiguration.GetArticleUseCase;
import com.conduit.application.article.ArticleUseCaseConfiguration.ListArticlesUseCase;
import com.conduit.application.article.ArticleUseCaseConfiguration.ListTagsUseCase;
import com.conduit.application.article.ArticleUseCaseConfiguration.UpdateArticleUseCase;
import com.conduit.application.user.UserRepository;
import com.conduit.domain.article.ArticleQuery;
import com.fasterxml.jackson.databind.JsonNode;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ArticleController {
  private final CreateArticleUseCase create;
  private final UpdateArticleUseCase update;
  private final DeleteArticleUseCase delete;
  private final GetArticleUseCase get;
  private final ListArticlesUseCase list;
  private final ListTagsUseCase tags;
  private final UserRepository users;
  private final MeterRegistry metrics;

  public ArticleController(
      CreateArticleUseCase create,
      UpdateArticleUseCase update,
      DeleteArticleUseCase delete,
      GetArticleUseCase get,
      ListArticlesUseCase list,
      ListTagsUseCase tags,
      UserRepository users,
      MeterRegistry metrics) {
    this.create = create;
    this.update = update;
    this.delete = delete;
    this.get = get;
    this.list = list;
    this.tags = tags;
    this.users = users;
    this.metrics = metrics;
  }

  @PostMapping("/articles")
  public ResponseEntity<ArticleResponseMapper.ArticleResponse> create(
      @RequestBody JsonNode body, HttpServletRequest request) {
    JsonNode article = requiredArticle(body);
    ArticleCommands.Create command =
        new ArticleCommands.Create(
            text(article, "title"),
            text(article, "description"),
            text(article, "body"),
            tags(article));
    var result = create.execute(userId(request), command);
    metrics.counter("conduit.article.operations", "operation", "create").increment();
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ArticleResponseMapper.toResponse(result, users));
  }

  @GetMapping("/articles/{slug}")
  public ArticleResponseMapper.ArticleResponse get(@PathVariable String slug) {
    metrics.counter("conduit.article.operations", "operation", "get").increment();
    return ArticleResponseMapper.toResponse(get.execute(slug), users);
  }

  @GetMapping("/articles")
  public ArticleResponseMapper.ArticleListResponse list(
      @RequestParam(required = false) String tag,
      @RequestParam(required = false) String author,
      @RequestParam(required = false) String limit,
      @RequestParam(required = false) String offset) {
    ArticleRepository.ArticlePage page = list.execute(query(author, tag, limit, offset));
    metrics.counter("conduit.article.operations", "operation", "list").increment();
    return ArticleResponseMapper.toListResponse(page.articles(), page.total(), users);
  }

  @GetMapping("/tags")
  public java.util.Map<String, List<String>> tags() {
    metrics.counter("conduit.article.operations", "operation", "tags").increment();
    return java.util.Map.of("tags", tags.execute());
  }

  @PutMapping("/articles/{slug}")
  public ArticleResponseMapper.ArticleResponse update(
      @PathVariable String slug, @RequestBody JsonNode body, HttpServletRequest request) {
    JsonNode article = requiredArticle(body);
    ArticleCommands.Update command =
        new ArticleCommands.Update(
            article.has("title"), nullableText(article, "title"),
            article.has("description"), nullableText(article, "description"),
            article.has("body"), nullableText(article, "body"),
            article.has("tagList"), tags(article));
    var result = update.execute(userId(request), slug, command);
    metrics.counter("conduit.article.operations", "operation", "update").increment();
    return ArticleResponseMapper.toResponse(result, users);
  }

  @DeleteMapping("/articles/{slug}")
  public ResponseEntity<Void> delete(@PathVariable String slug, HttpServletRequest request) {
    delete.execute(userId(request), slug);
    metrics.counter("conduit.article.operations", "operation", "delete").increment();
    return ResponseEntity.noContent().build();
  }

  private static JsonNode requiredArticle(JsonNode body) {
    if (body == null || !body.has("article") || !body.get("article").isObject()) {
      throw new ArticleException("article", "is required", ArticleException.ErrorKind.VALIDATION);
    }
    return body.get("article");
  }

  private static UUID userId(HttpServletRequest request) {
    Object value = request.getAttribute(TokenAuthenticationFilter.CURRENT_USER_ID);
    if (value instanceof UUID id) return id;
    throw new ArticleException("token", "is missing", ArticleException.ErrorKind.UNAUTHORIZED);
  }

  private static String text(JsonNode node, String field) {
    if (!node.has(field) || node.get(field).isNull()) return null;
    return node.get(field).asText();
  }

  private static String nullableText(JsonNode node, String field) {
    if (!node.has(field)) return null;
    return node.get(field).isNull() ? null : node.get(field).asText();
  }

  private static List<String> tags(JsonNode node) {
    if (!node.has("tagList")) return List.of();
    if (node.get("tagList").isNull() || !node.get("tagList").isArray()) return null;
    List<String> values = new java.util.ArrayList<>();
    node.get("tagList").forEach(value -> values.add(value.isNull() ? null : value.asText()));
    return values;
  }

  private static Integer number(String field, String value) {
    if (value == null) return null;
    try {
      return Integer.valueOf(value);
    } catch (NumberFormatException exception) {
      throw new ArticleException(field, "is invalid", ArticleException.ErrorKind.VALIDATION);
    }
  }

  private static ArticleQuery query(String author, String tag, String limit, String offset) {
    try {
      return new ArticleQuery(author, tag, number("limit", limit), number("offset", offset));
    } catch (IllegalArgumentException exception) {
      String field =
          switch (exception.getMessage()) {
            case "offset" -> "offset";
            case "limit" -> "limit";
            default -> "tag";
          };
      throw new ArticleException(field, "is invalid", ArticleException.ErrorKind.VALIDATION);
    }
  }
}
