package com.conduit.application.article;

import java.util.List;

public final class ArticleCommands {
    private ArticleCommands() { }

    public record Create(String title, String description, String body, List<String> tagList) { }

    public record Update(
            boolean titlePresent, String title,
            boolean descriptionPresent, String description,
            boolean bodyPresent, String body,
            boolean tagsPresent, List<String> tagList) { }
}