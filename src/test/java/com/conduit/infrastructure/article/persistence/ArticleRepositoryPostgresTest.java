package com.conduit.infrastructure.article.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.conduit.application.article.ArticleRepository;
import com.conduit.application.user.UserRepository;
import com.conduit.domain.article.Article;
import com.conduit.domain.article.ArticleQuery;
import com.conduit.domain.article.Tag;
import com.conduit.domain.user.User;
import com.conduit.infrastructure.user.persistence.UserRepositoryAdapter;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

@DataJpaTest
@Import({ArticleRepositoryAdapter.class, UserRepositoryAdapter.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EnabledIf(
    value = "isDockerAvailable",
    disabledReason = "Docker daemon is unavailable; H2 tests remain active")
class ArticleRepositoryPostgresTest {
  @SuppressWarnings("resource")
  static final PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>("postgres:16-alpine")
          .withDatabaseName("conduit")
          .withUsername("conduit")
          .withPassword("test-only");

  @Autowired ArticleRepository articles;
  @Autowired UserRepository users;

  @DynamicPropertySource
  static void configureDatabase(DynamicPropertyRegistry registry) {
    postgres.start();
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
    registry.add("spring.flyway.enabled", () -> true);
  }

  @AfterAll
  static void stopDatabase() {
    postgres.close();
  }

  static boolean isDockerAvailable() {
    try {
      Process process =
          new ProcessBuilder("docker", "info", "--format", "{{.ServerVersion}}")
              .redirectErrorStream(true)
              .start();
      boolean completed = process.waitFor(10, TimeUnit.SECONDS);
      if (!completed) {
        process.destroyForcibly();
        return false;
      }
      return process.exitValue() == 0;
    } catch (Exception exception) {
      return false;
    }
  }

  @Test
  @SuppressWarnings("null")
  void persistsFiltersPaginatesAndDeletesArticlesThroughPostgres() {
    User persistedUser =
        users.save(User.create("article-pg@example.invalid", "article-pg", "hash", null, null));
    UUID persistedAuthorId = persistedUser.id();
    Article article =
        new Article(
            UUID.randomUUID(),
            persistedAuthorId,
            "postgres-article",
            "Postgres article",
            "Description",
            "Body",
            List.of(new Tag("Java")),
            Instant.now(),
            Instant.now());

    articles.save(article);

    assertEquals("postgres-article", articles.findBySlug("postgres-article").orElseThrow().slug());
    assertEquals(1, articles.findPage(new ArticleQuery("article-pg", "java", 1, 0)).total());
    assertEquals(List.of("java"), articles.findTags());
    articles.delete(article);
    assertEquals(0, articles.findPage(new ArticleQuery(null, null, 20, 0)).total());
  }

  @Test
  @SuppressWarnings("null")
  void paginatesACollectionOfAtLeastOneHundredArticles() {
    User user = users.save(User.create("scale@example.invalid", "scale-user", "hash", null, null));
    Instant base = Instant.now();
    for (int index = 0; index < 100; index++) {
      UUID id = UUID.randomUUID();
      articles.save(
          new Article(
              id,
              user.id(),
              "scale-" + index,
              "Scale " + index,
              "Description",
              "Body",
              List.of(new Tag("scale")),
              base.plusSeconds(index),
              base.plusSeconds(index)));
    }

    ArticleRepository.ArticlePage page =
        articles.findPage(new ArticleQuery("scale-user", "scale", 10, 90));

    assertEquals(100, page.total());
    assertEquals(10, page.articles().size());
    assertEquals("Scale 9", page.articles().get(0).title());
  }
}
