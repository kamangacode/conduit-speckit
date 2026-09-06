package com.conduit.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SensitiveDataRedactionTest {
    private static final String RAW_PASSWORD = "correct-horse-battery-staple";

    @Autowired MockMvc mockMvc;

    @Test
    void excludesPasswordAndPasswordHashFromSuccessfulResponse() throws Exception {
        String response = mockMvc.perform(post("/api/users")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"user":{"username":"redaction-user","email":"redaction@example.invalid","password":"%s"}}
                                """.formatted(RAW_PASSWORD)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        assertFalse(response.contains(RAW_PASSWORD));
        assertFalse(response.contains("passwordHash"));
        assertFalse(response.contains("\"password\""));
    }

    @Test
    void excludesRawPasswordFromValidationErrors() throws Exception {
        String response = mockMvc.perform(post("/api/users")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"user":{"username":"redaction-invalid","email":"redaction-invalid@example.invalid","password":"%s"}}
                                """.formatted(RAW_PASSWORD.substring(0, 4))))
                .andExpect(status().isUnprocessableEntity())
                .andReturn().getResponse().getContentAsString();

        assertFalse(response.contains(RAW_PASSWORD));
        assertFalse(response.contains("passwordHash"));
    }
}
