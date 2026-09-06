package com.conduit.application.article;

import com.conduit.domain.article.Article;
import com.conduit.domain.article.ArticleQuery;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ArticleRepository {
    Article save(Article article);
    Optional<Article> findBySlug(String slug);
    ArticlePage findPage(ArticleQuery query);
    List<String> findTags();
    void delete(Article article);

    record ArticlePage(List<Article> articles, long total) {
        public ArticlePage { articles = List.copyOf(articles); }
    }
}