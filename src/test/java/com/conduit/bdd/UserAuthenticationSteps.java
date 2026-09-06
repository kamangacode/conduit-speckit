package com.conduit.bdd;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class UserAuthenticationSteps {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    private MvcResult result;
    private String registrationBody;

    @Given("no member uses the email {string}")
    public void noMemberUsesEmail(String email) {
        registrationBody = "";
    }

    @When("the visitor registers with:")
    public void visitorRegistersWith(DataTable table) throws Exception {
        Map<String, String> fields = table.asMap(String.class, String.class);
        registrationBody = """ 
                {"user":{"username":"%s","email":"%s","password":"%s"}}
                """.formatted(fields.get("username"), fields.get("email"), fields.get("password"));
        result = mockMvc.perform(post("/api/users")
                        .contentType("application/json")
                        .content(registrationBody))
                .andReturn();
    }

    @When("the visitor requests the current user without a token")
    public void visitorRequestsCurrentUserWithoutToken() throws Exception {
        result = mockMvc.perform(get("/api/user")).andReturn();
    }

    @Then("the response status is {int}")
    public void responseStatusIs(int status) {
        assertEquals(status, result.getResponse().getStatus());
    }

    @Then("the response contains a user with username {string}")
    public void responseContainsUserWithUsername(String username) throws Exception {
        JsonNode user = objectMapper.readTree(result.getResponse().getContentAsString()).path("user");
        assertEquals(username, user.path("username").asText());
    }

    @Then("the response does not contain a password")
    public void responseDoesNotContainPassword() throws Exception {
        JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString());
        assertFalse(response.path("user").has("password"));
        assertFalse(response.path("user").has("passwordHash"));
    }

    @Then("the response does not contain account data")
    public void responseDoesNotContainAccountData() throws Exception {
        JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString());
        assertTrue(response.path("user").isMissingNode());
    }
}