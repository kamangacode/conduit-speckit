package com.conduit.article;

import com.conduit.application.user.UserRepository;
import com.conduit.test.InMemoryUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ArticleAcceptanceTest {
    @Autowired MockMvc mockMvc;

    @Test
    void createsListsUpdatesAndDeletesAnArticle() throws Exception {
        String token = register("article-author");
        String article = """
                {"article":{"title":"Hello World","description":"A description","body":"The body","tagList":["Java","java","Spring"]}}
                """;

        String response = mockMvc.perform(post("/api/articles").header("Authorization", "Token " + token)
                        .contentType("application/json").content(article))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.article.body").value("The body"))
                .andExpect(jsonPath("$.article.tagList").isArray())
                .andExpect(jsonPath("$.article.tagList[0]").value("java"))
                .andExpect(jsonPath("$.article.tagList[1]").value("spring"))
                .andReturn().getResponse().getContentAsString();
        String slug = response.replaceAll(".*\\\"slug\\\":\\\"([^\\\"]+)\\\".*", "$1");

        mockMvc.perform(get("/api/articles")).andExpect(status().isOk())
                .andExpect(jsonPath("$.articles[0].body").doesNotExist())
                .andExpect(jsonPath("$.articlesCount").value(1));
        String updated = mockMvc.perform(put("/api/articles/" + slug).header("Authorization", "Token " + token)
                        .contentType("application/json").content("{\"article\":{\"title\":\"Updated\"}}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.article.description").value("A description"))
                .andReturn().getResponse().getContentAsString();
        String updatedSlug = updated.replaceAll(".*\\\"slug\\\":\\\"([^\\\"]+)\\\".*", "$1");
        mockMvc.perform(delete("/api/articles/" + updatedSlug).header("Authorization", "Token " + token))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/articles/" + updatedSlug)).andExpect(status().isNotFound());
    }

    @Test
    void rejectsInvalidPaginationAndMissingAuthentication() throws Exception {
        mockMvc.perform(get("/api/articles?limit=-1")).andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors.limit").exists());
        mockMvc.perform(post("/api/articles").contentType("application/json")
                        .content("{\"article\":{\"title\":\"x\",\"description\":\"d\",\"body\":\"b\"}}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void preservesClearsAndRejectsNullTags() throws Exception {
        String token = register("tag-author");
        String response = mockMvc.perform(post("/api/articles").header("Authorization", "Token " + token)
                        .contentType("application/json").content("{\"article\":{\"title\":\"Tags\",\"description\":\"d\",\"body\":\"b\",\"tagList\":[\"one\"]}}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        String slug = response.replaceAll(".*\\\"slug\\\":\\\"([^\\\"]+)\\\".*", "$1");
        mockMvc.perform(put("/api/articles/" + slug).header("Authorization", "Token " + token)
                        .contentType("application/json").content("{\"article\":{\"body\":\"changed\"}}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.article.tagList[0]").value("one"));
        mockMvc.perform(put("/api/articles/" + slug).header("Authorization", "Token " + token)
                        .contentType("application/json").content("{\"article\":{\"tagList\":[]}}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.article.tagList").isEmpty());
        mockMvc.perform(put("/api/articles/" + slug).header("Authorization", "Token " + token)
                        .contentType("application/json").content("{\"article\":{\"tagList\":null}}"))
                .andExpect(status().isUnprocessableEntity()).andExpect(jsonPath("$.errors.tagList").exists());
    }

    @Test
    void rejectsMutationByAnotherAuthor() throws Exception {
        String ownerToken = register("owner");
        String response = mockMvc.perform(post("/api/articles").header("Authorization", "Token " + ownerToken)
                        .contentType("application/json").content("{\"article\":{\"title\":\"Owned\",\"description\":\"d\",\"body\":\"b\"}}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        String slug = response.replaceAll(".*\\\"slug\\\":\\\"([^\\\"]+)\\\".*", "$1");
        String otherToken = register("other");
        mockMvc.perform(delete("/api/articles/" + slug).header("Authorization", "Token " + otherToken))
                .andExpect(status().isForbidden()).andExpect(jsonPath("$.errors.article").exists());
    }

    @Test
    void filtersPaginatesAndListsNormalizedTags() throws Exception {
        String token = register("reader");
        mockMvc.perform(post("/api/articles").header("Authorization", "Token " + token)
                        .contentType("application/json").content("{\"article\":{\"title\":\"First\",\"description\":\"d\",\"body\":\"b\",\"tagList\":[\"Java\"]}}"))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/articles").header("Authorization", "Token " + token)
                        .contentType("application/json").content("{\"article\":{\"title\":\"Second\",\"description\":\"d\",\"body\":\"b\",\"tagList\":[\"Spring\"]}}"))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/api/articles?author=reader&limit=1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.articlesCount").value(2)).andExpect(jsonPath("$.articles").isArray())
                .andExpect(jsonPath("$.articles[0].body").doesNotExist());
        mockMvc.perform(get("/api/articles?tag=java")).andExpect(status().isOk())
                .andExpect(jsonPath("$.articlesCount").value(1));
        mockMvc.perform(get("/api/articles?tag=missing")).andExpect(status().isOk())
                .andExpect(jsonPath("$.articlesCount").value(0)).andExpect(jsonPath("$.articles").isEmpty());
        mockMvc.perform(get("/api/tags")).andExpect(status().isOk())
                .andExpect(jsonPath("$.tags").isArray()).andExpect(jsonPath("$.tags[0]").value("java"))
                .andExpect(jsonPath("$.tags[1]").value("spring"));
    }

    @Test
    void returnsNewestFirstAndOffsetPage() throws Exception {
        String token = register("pager");
        mockMvc.perform(post("/api/articles").header("Authorization", "Token " + token)
                        .contentType("application/json").content("{\"article\":{\"title\":\"Old\",\"description\":\"d\",\"body\":\"b\"}}"))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/articles").header("Authorization", "Token " + token)
                        .contentType("application/json").content("{\"article\":{\"title\":\"New\",\"description\":\"d\",\"body\":\"b\"}}"))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/api/articles?limit=1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.articlesCount").value(2)).andExpect(jsonPath("$.articles[0].title").value("New"));
        mockMvc.perform(get("/api/articles?limit=1&offset=1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.articlesCount").value(2)).andExpect(jsonPath("$.articles[0].title").value("Old"));
    }

    private String register(String username) throws Exception {
        return mockMvc.perform(post("/api/users").contentType("application/json")
                        .content("{\"user\":{\"username\":\"" + username + "\",\"email\":\"" + username + "@example.invalid\",\"password\":\"password123\"}}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString()
                .replaceAll(".*\\\"token\\\":\\\"([^\\\"]+)\\\".*", "$1");
    }

    @TestConfiguration
    static class TestConfig {
        @Bean @Primary UserRepository userRepository() { return new InMemoryUserRepository(); }
    }
}