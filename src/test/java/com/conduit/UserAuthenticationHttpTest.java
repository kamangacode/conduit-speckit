package com.conduit;

import com.conduit.application.user.UserRepository;
import com.conduit.test.InMemoryUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserAuthenticationHttpTest {
    @Autowired MockMvc mockMvc;

    @Test
    @Tags({
            @Tag("AC-US1-001"), @Tag("AC-US1-004"), @Tag("AC-US2-001"),
            @Tag("AC-US3-001"), @Tag("AC-US3-003"), @Tag("AC-US4-001"),
            @Tag("AC-US4-004"), @Tag("AC-US4-005")
    })
    void registersLogsInReadsAndUpdatesCurrentUserWithoutPassword() throws Exception {
        String registration = """
                {"user":{"username":"alice","email":"alice@example.invalid","password":"password123"}}
                """;
        String response = mockMvc.perform(post("/api/users").contentType("application/json").content(registration))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.user.username").value("alice"))
                .andExpect(jsonPath("$.user.password").doesNotExist())
                .andReturn().getResponse().getContentAsString();
        String token = response.replaceAll(".*\\\"token\\\":\\\"([^\\\"]+)\\\".*", "$1");

        mockMvc.perform(post("/api/users/login").contentType("application/json").content("""
                        {"user":{"email":"alice@example.invalid","password":"password123"}}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.username").value("alice"))
                .andExpect(jsonPath("$.user.password").doesNotExist());

        mockMvc.perform(get("/api/user").header("Authorization", "Token " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.email").value("alice@example.invalid"));

        mockMvc.perform(put("/api/user").header("Authorization", "Token " + token)
                        .contentType("application/json").content("""
                                {"user":{"bio":"Hello"}}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.bio").value("Hello"));
    }

    @Test
    @Tags({
            @Tag("AC-US1-002"), @Tag("AC-US1-003"), @Tag("AC-US2-002"),
            @Tag("AC-US4-002"), @Tag("AC-US4-003")
    })
    void rejectsDuplicateAndInvalidRequestsAtHttpBoundary() throws Exception {
        String request = """
                {"user":{"username":"bob","email":"bob@example.invalid","password":"password123"}}
                """;
        mockMvc.perform(post("/api/users").contentType("application/json").content(request))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/users").contentType("application/json").content(request))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.errors.email").exists());
        mockMvc.perform(post("/api/users").contentType("application/json").content("""
                        {"user":{"username":"short","email":"short@example.invalid","password":"short"}}
                        """))
                .andExpect(status().isUnprocessableEntity()).andExpect(jsonPath("$.errors.password").exists());
    }

    @Test
    @Tags({
            @Tag("AC-US2-003"), @Tag("AC-US3-002"), @Tag("AC-US4-006")
    })
    void rejectsMissingOrBearerAuthentication() throws Exception {
        mockMvc.perform(get("/api/user")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/user").header("Authorization", "Bearer invalid"))
                .andExpect(status().isUnauthorized());
    }

    @TestConfiguration
    static class TestConfig {
        @Bean @Primary UserRepository userRepository() { return new InMemoryUserRepository(); }
    }
}
