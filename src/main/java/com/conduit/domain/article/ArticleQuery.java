package com.conduit.domain.article;

public record ArticleQuery(String author, String tag, Integer limit, Integer offset) {
    public ArticleQuery {
        if (limit != null && (limit < 0 || limit > 100)) {
            throw new IllegalArgumentException("limit");
        }
        if (offset != null && offset < 0) {
            throw new IllegalArgumentException("offset");
        }
        tag = tag == null || tag.isBlank() ? null : new Tag(tag).value();
        limit = limit == null ? 20 : limit;
        offset = offset == null ? 0 : offset;
    }
}