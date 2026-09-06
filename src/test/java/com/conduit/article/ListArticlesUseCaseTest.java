package com.conduit.article;

import com.conduit.domain.article.ArticleQuery;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ListArticlesUseCaseTest {
    @Test
    void normalizesFiltersAndAppliesDefaultPaging() {
        ArticleQuery query = new ArticleQuery("author", "Java", null, null);

        assertEquals("java", query.tag());
        assertEquals(20, query.limit());
        assertEquals(0, query.offset());
    }

    @Test
    void rejectsInvalidPagingValues() {
        assertThrows(IllegalArgumentException.class, () -> new ArticleQuery(null, null, -1, 0));
        assertThrows(IllegalArgumentException.class, () -> new ArticleQuery(null, null, 1, -1));
    }
}