package com.conduit.infrastructure.persistence;

import com.conduit.application.article.ArticleRepository;
import com.conduit.application.user.UserRepository;
import com.conduit.domain.article.Article;
import com.conduit.domain.article.ArticleQuery;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

@Repository
public class ArticleRepositoryAdapter implements ArticleRepository {
    private final SpringDataArticleRepository repository;
    private final UserRepository users;

    public ArticleRepositoryAdapter(SpringDataArticleRepository repository, UserRepository users) {
        this.repository = repository;
        this.users = users;
    }

    @Override
    @Transactional
    @SuppressWarnings("null")
    public Article save(Article article) {
        return repository.save(ArticleEntity.fromDomain(article)).toDomain();
    }

    @Override
    @SuppressWarnings("null")
    public Optional<Article> findBySlug(String slug) {
        return repository.findBySlug(slug).map(ArticleEntity::toDomain);
    }

    @Override
    @SuppressWarnings("null")
    public ArticlePage findPage(ArticleQuery query) {
        List<ArticleEntity> entities = repository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Article> filtered = entities.stream()
                .filter(article -> query.tag() == null || article.getTags().contains(query.tag()))
                .filter(article -> query.author() == null || users.findById(article.getAuthorId())
                        .map(user -> user.username().equals(query.author())).orElse(false))
                .map(ArticleEntity::toDomain)
                .toList();
        int from = Math.min(query.offset(), filtered.size());
        int to = Math.min(from + query.limit(), filtered.size());
        return new ArticlePage(filtered.subList(from, to), filtered.size());
    }

    @Override
    public List<String> findTags() {
        return repository.findAll().stream()
                .flatMap(article -> article.getTags().stream())
                .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new))
                .stream().sorted().toList();
    }

    @Override
    @Transactional
    @SuppressWarnings("null")
    public void delete(Article article) {
        repository.deleteById(article.id());
    }
}