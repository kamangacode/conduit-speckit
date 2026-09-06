package com.conduit.article;

import com.conduit.domain.article.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ListTagsUseCaseTest {
    @Test
    void normalizesTagValuesForCatalogueQueries() {
        assertEquals("java", new Tag(" Java ").value());
        assertEquals("spring", new Tag("SPRING").value());
    }
}