package com.conduit.domain.article;

import java.util.Locale;

public record Tag(String value) {
    public Tag {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("tag must not be blank");
        }
        value = value.trim().toLowerCase(Locale.ROOT);
    }
}