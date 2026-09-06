package com.conduit.application.article;

import com.conduit.application.user.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ArticleUseCaseConfiguration {
    @Bean CreateArticleUseCase createArticleUseCase(ArticleRepository articles) {
        return new CreateArticleUseCase(ArticleUseCases.create(articles));
    }
    @Bean UpdateArticleUseCase updateArticleUseCase(ArticleRepository articles) {
        return new UpdateArticleUseCase(ArticleUseCases.update(articles));
    }
    @Bean DeleteArticleUseCase deleteArticleUseCase(ArticleRepository articles) {
        return new DeleteArticleUseCase(ArticleUseCases.delete(articles));
    }
    @Bean GetArticleUseCase getArticleUseCase(ArticleRepository articles) {
        return new GetArticleUseCase(ArticleUseCases.get(articles));
    }
    @Bean ListArticlesUseCase listArticlesUseCase(ArticleRepository articles) {
        return new ListArticlesUseCase(ArticleUseCases.list(articles));
    }
    @Bean ListTagsUseCase listTagsUseCase(ArticleRepository articles) {
        return new ListTagsUseCase(ArticleUseCases.tags(articles));
    }

    public record CreateArticleUseCase(ArticleUseCases.CreateArticle delegate) {
        public com.conduit.domain.article.Article execute(java.util.UUID authorId, ArticleCommands.Create command) { return delegate.execute(authorId, command); }
    }
    public record UpdateArticleUseCase(ArticleUseCases.UpdateArticle delegate) {
        public com.conduit.domain.article.Article execute(java.util.UUID authorId, String slug, ArticleCommands.Update command) { return delegate.execute(authorId, slug, command); }
    }
    public record DeleteArticleUseCase(ArticleUseCases.DeleteArticle delegate) {
        public void execute(java.util.UUID authorId, String slug) { delegate.execute(authorId, slug); }
    }
    public record GetArticleUseCase(ArticleUseCases.GetArticle delegate) {
        public com.conduit.domain.article.Article execute(String slug) { return delegate.execute(slug); }
    }
    public record ListArticlesUseCase(ArticleUseCases.ListArticles delegate) {
        public ArticleRepository.ArticlePage execute(com.conduit.domain.article.ArticleQuery query) { return delegate.execute(query); }
    }
    public record ListTagsUseCase(ArticleUseCases.ListTags delegate) {
        public java.util.List<String> execute() { return delegate.execute(); }
    }
}