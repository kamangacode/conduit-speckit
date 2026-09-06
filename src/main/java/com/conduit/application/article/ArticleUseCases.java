package com.conduit.application.article;

import com.conduit.domain.article.Article;
import com.conduit.domain.article.ArticleQuery;
import com.conduit.domain.article.Tag;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

public final class ArticleUseCases {
    private ArticleUseCases() { }

    public static CreateArticle create(ArticleRepository articles) {
        return new CreateArticle(articles);
    }

    public static UpdateArticle update(ArticleRepository articles) {
        return new UpdateArticle(articles);
    }

    public static DeleteArticle delete(ArticleRepository articles) {
        return new DeleteArticle(articles);
    }

    public static GetArticle get(ArticleRepository articles) {
        return new GetArticle(articles);
    }

    public static ListArticles list(ArticleRepository articles) {
        return new ListArticles(articles);
    }

    public static ListTags tags(ArticleRepository articles) {
        return new ListTags(articles);
    }

    static String slug(String title, UUID id) {
        String normalized = title.trim().toLowerCase().replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        return (normalized.isBlank() ? "article" : normalized) + "-" + id.toString().substring(0, 8);
    }

    static List<Tag> tags(List<String> values) {
        if (values == null) {
            throw new ArticleException("tagList", "must be an array", ArticleException.ErrorKind.VALIDATION);
        }
        LinkedHashSet<Tag> normalized = new LinkedHashSet<>();
        for (String value : values) {
            try {
                normalized.add(new Tag(value));
            } catch (IllegalArgumentException exception) {
                throw new ArticleException("tagList", "contains an invalid tag", ArticleException.ErrorKind.VALIDATION);
            }
        }
        return new ArrayList<>(normalized);
    }

    static void required(String field, String value) {
        if (value == null || value.isBlank()) {
            throw new ArticleException(field, "can't be blank", ArticleException.ErrorKind.VALIDATION);
        }
    }

    public static final class CreateArticle {
        private final ArticleRepository articles;
        CreateArticle(ArticleRepository articles) { this.articles = articles; }

        public Article execute(UUID authorId, ArticleCommands.Create command) {
            required("title", command.title());
            required("description", command.description());
            required("body", command.body());
            UUID id = UUID.randomUUID();
            Instant now = Instant.now();
            return articles.save(new Article(id, authorId, slug(command.title(), id), command.title().trim(),
                    command.description().trim(), command.body(), tags(command.tagList()), now, now));
        }
    }

    public static final class UpdateArticle {
        private final ArticleRepository articles;
        UpdateArticle(ArticleRepository articles) { this.articles = articles; }

        public Article execute(UUID authorId, String slug, ArticleCommands.Update command) {
            Article current = articles.findBySlug(slug).orElseThrow(() -> notFound(slug));
            ensureOwner(authorId, current);
            String title = command.titlePresent() ? command.title() : current.title();
            String description = command.descriptionPresent() ? command.description() : current.description();
            String body = command.bodyPresent() ? command.body() : current.body();
            required("title", title);
            required("description", description);
            required("body", body);
            List<Tag> tags = command.tagsPresent() ? tags(command.tagList()) : current.tags();
            String nextSlug = command.titlePresent() ? slug(title, current.id()) : current.slug();
            return articles.save(current.update(nextSlug, title.trim(), description.trim(), body, tags, Instant.now()));
        }
    }

    public static final class DeleteArticle {
        private final ArticleRepository articles;
        DeleteArticle(ArticleRepository articles) { this.articles = articles; }

        public void execute(UUID authorId, String slug) {
            Article current = articles.findBySlug(slug).orElseThrow(() -> notFound(slug));
            ensureOwner(authorId, current);
            articles.delete(current);
        }
    }

    public static final class GetArticle {
        private final ArticleRepository articles;
        GetArticle(ArticleRepository articles) { this.articles = articles; }

        public Article execute(String slug) {
            return articles.findBySlug(slug).orElseThrow(() -> notFound(slug));
        }
    }

    public static final class ListArticles {
        private final ArticleRepository articles;
        ListArticles(ArticleRepository articles) { this.articles = articles; }

        public ArticleRepository.ArticlePage execute(ArticleQuery query) { return articles.findPage(query); }
    }

    public static final class ListTags {
        private final ArticleRepository articles;
        ListTags(ArticleRepository articles) { this.articles = articles; }

        public List<String> execute() { return articles.findTags(); }
    }

    private static ArticleException notFound(String slug) {
        return new ArticleException("article", "not found", ArticleException.ErrorKind.NOT_FOUND);
    }

    private static void ensureOwner(UUID authorId, Article article) {
        if (!article.authorId().equals(authorId)) {
            throw new ArticleException("article", "forbidden", ArticleException.ErrorKind.FORBIDDEN);
        }
    }
}