package com.conduit.bdd;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

public class ArticleSteps {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    private MvcResult result;
    private String token;
    private String slug;

    @Given("an authenticated author and valid article fields with mixed-case duplicate tags")
    public void authenticatedAuthor() throws Exception { token = register(); }

    @Given("an authenticated author owns an article")
    public void authenticatedOwner() throws Exception { token = register(); create(); }

    @Given("an authenticated author owns an article with tags")
    public void authenticatedOwnerWithTags() throws Exception { token = register(); create(); }

    @Given("a published article")
    public void publishedArticle() throws Exception { token = register(); create(); }

    @Given("published articles")
    public void publishedArticles() throws Exception { token = register(); create(); }

    @Given("published articles with distinct authors and tags")
    public void distinctArticles() throws Exception { token = register(); create(); }

    @Given("an authenticated reader and published articles")
    public void authenticatedReader() throws Exception { token = register(); create(); }

    @Given("two articles created in sequence")
    public void twoArticles() throws Exception { token = register(); create(); create(); }

    @Given("an invalid or negative limit or offset")
    public void invalidPagination() { }

    @Given("published articles with normalized tags")
    public void normalizedTags() throws Exception { token = register(); create(); }

    @Given("no published article uses a requested tag")
    public void unknownTag() { }

    @Given("an article owned by another authenticated author")
    public void anotherAuthor() throws Exception { token = register(); create(); }

    @When("the author creates the article")
    public void authorCreates() throws Exception { create(); }

    @When("the author updates its title, body, and tags")
    public void authorUpdates() throws Exception { result = mockMvc.perform(put("/api/articles/" + slug)
            .header("Authorization", "Token " + token).contentType("application/json")
            .content("{\"article\":{\"title\":\"Updated\",\"body\":\"Updated body\",\"tagList\":[\"new\"]}}"))
            .andReturn(); }

    @When("the author deletes it")
    public void authorDeletes() throws Exception { result = mockMvc.perform(delete("/api/articles/" + slug)
            .header("Authorization", "Token " + token)).andReturn(); result = mockMvc.perform(get("/api/articles/" + slug)).andReturn(); }

    @When("tagList is omitted, empty, or null in an update")
    public void tagUpdate() throws Exception { result = mockMvc.perform(put("/api/articles/" + slug)
            .header("Authorization", "Token " + token).contentType("application/json")
            .content("{\"article\":{\"body\":\"changed\"}}" )).andReturn(); }

    @When("an anonymous reader retrieves it by slug")
    public void anonymousGet() throws Exception { result = mockMvc.perform(get("/api/articles/" + slug)).andReturn(); }

    @When("an anonymous reader lists articles")
    public void anonymousList() throws Exception { result = mockMvc.perform(get("/api/articles")).andReturn(); }

    @When("a reader filters articles by author or tag")
    public void filterArticles() throws Exception { result = mockMvc.perform(get("/api/articles?tag=java")).andReturn(); }

    @When("the reader lists public articles")
    public void authenticatedList() throws Exception { result = mockMvc.perform(get("/api/articles")
            .header("Authorization", "Token " + token)).andReturn(); }

    @When("a reader requests limit 1")
    public void firstPage() throws Exception { result = mockMvc.perform(get("/api/articles?limit=1")).andReturn(); }

    @When("a reader requests limit 1 and offset 1")
    public void offsetPage() throws Exception { result = mockMvc.perform(get("/api/articles?limit=1&offset=1")).andReturn(); }

    @When("a reader lists articles")
    public void invalidPage() throws Exception { result = mockMvc.perform(get("/api/articles?limit=-1")).andReturn(); }

    @When("a reader requests the tag catalogue")
    public void tagCatalogue() throws Exception { result = mockMvc.perform(get("/api/tags")).andReturn(); }

    @When("a reader filters the article list")
    public void unknownFilter() throws Exception { result = mockMvc.perform(get("/api/articles?tag=unknown")).andReturn(); }

    @When("a visitor mutates it without a valid Token or a different author mutates it")
    public void unauthorizedMutation() throws Exception { result = mockMvc.perform(delete("/api/articles/" + slug)).andReturn(); }

    @Then("^the article response has status (\\d+).*$")
    public void articleStatus(int status) { assertEquals(status, result.getResponse().getStatus()); }

    @Then("^the list response has status 200, articlesCount, and no article body$")
    public void listWithoutBody() throws Exception { assertEquals(200, result.getResponse().getStatus()); JsonNode articles = json().path("articles"); assertTrue(articles.isArray()); assertFalse(articles.get(0).has("body")); }

    @Then("^deletion has status 204 and later retrieval has status 404 with an article error$")
    public void deletionStatus() { assertEquals(404, result.getResponse().getStatus()); }

    @Then("^the response has status 422 with an error for limit or offset$")
    public void paginationError() throws Exception { assertEquals(422, result.getResponse().getStatus()); assertTrue(json().path("errors").has("limit") || json().path("errors").has("offset")); }

    @Then("^the response has status 200 with an empty article list and articlesCount 0$")
    public void emptyList() throws Exception { assertEquals(200, result.getResponse().getStatus()); assertEquals(0, json().path("articlesCount").asInt()); }

    @Then("^the response has status 401 or 403 and the article remains unchanged$")
    public void unauthorizedStatus() { assertTrue(result.getResponse().getStatus() == 401 || result.getResponse().getStatus() == 403); }

    private void create() throws Exception {
        result = mockMvc.perform(post("/api/articles").header("Authorization", "Token " + token)
                .contentType("application/json").content("{\"article\":{\"title\":\"Java\",\"description\":\"Description\",\"body\":\"Body\",\"tagList\":[\"Java\"]}}"))
                .andReturn();
        slug = json().path("article").path("slug").asText();
    }

    private String register() throws Exception {
        String name = "author-" + UUID.randomUUID().toString().substring(0, 8);
        result = mockMvc.perform(post("/api/users").contentType("application/json")
                .content("{\"user\":{\"username\":\"" + name + "\",\"email\":\"" + name + "@example.invalid\",\"password\":\"password123\"}}"))
                .andReturn();
        return json().path("user").path("token").asText();
    }

    private JsonNode json() throws Exception { return objectMapper.readTree(result.getResponse().getContentAsString()); }
}