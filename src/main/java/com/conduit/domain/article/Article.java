package com.conduit.domain.article;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record Article(
        UUID id,
        UUID authorId,
        String slug,
        String title,
        String description,
        String body,
        List<Tag> tags,
        Instant createdAt,
        Instant updatedAt) {
    public Article {
        if (id == null || authorId == null || slug == null || title == null || title.isBlank()
                || description == null || description.isBlank() || body == null || body.isBlank()
                || createdAt == null || updatedAt == null) {
            throw new IllegalArgumentException("article fields are invalid");
        }
        tags = List.copyOf(tags == null ? List.of() : tags);
    }

    public Article update(String nextSlug, String nextTitle, String nextDescription, String nextBody,
                         List<Tag> nextTags, Instant nextUpdatedAt) {
        return new Article(id, authorId, nextSlug, nextTitle, nextDescription, nextBody,
                nextTags, createdAt, nextUpdatedAt);
    }

    public List<String> tagValues() {
        return tags.stream().map(tag -> tag.value()).toList();
    }
}