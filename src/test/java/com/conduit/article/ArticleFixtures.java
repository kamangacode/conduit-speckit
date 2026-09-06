package com.conduit.article;

public final class ArticleFixtures {
    private ArticleFixtures() { }

    public static String createRequest(String title, String description, String body, String tags) {
        return "{\"article\":{\"title\":\"%s\",\"description\":\"%s\",\"body\":\"%s\",\"tagList\":%s}}"
                .formatted(title, description, body, tags);
    }
}