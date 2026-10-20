package com.kkotlyarenko.microblog;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Интеграционные тесты безопасности API.
 * Поднимают приложение целиком (in-memory H2) и проверяют ключевые сценарии.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ApiSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String registerAndLogin(String username, String password) throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isCreated());

        String body = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode node = objectMapper.readTree(body);
        return node.get("accessToken").stringValue();
    }

    @Test
    void protectedEndpointWithoutTokenReturns401() throws Exception {
        mockMvc.perform(get("/api/data"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpointWithValidTokenReturns200() throws Exception {
        String username = "user_" + UUID.randomUUID().toString().substring(0, 8);
        String token = registerAndLogin(username, "StrongPass123");

        mockMvc.perform(get("/api/data")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void invalidTokenIsRejected() throws Exception {
        mockMvc.perform(get("/api/data")
                        .header("Authorization", "Bearer not.a.real.token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void wrongPasswordIsRejected() throws Exception {
        String username = "user_" + UUID.randomUUID().toString().substring(0, 8);
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"StrongPass123\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"WrongPass123\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void sqlInjectionInLoginDoesNotBypassAuth() throws Exception {
        // Классическая попытка обхода аутентификации через SQLi.
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"' OR '1'='1\",\"password\":\"' OR '1'='1\"}"))
                .andExpect(status().isBadRequest()); // ломается regex-валидация логина
    }

    @Test
    void userSuppliedDataIsEscapedInResponses() throws Exception {
        String username = "user_" + UUID.randomUUID().toString().substring(0, 8);
        String token = registerAndLogin(username, "StrongPass123");

        String payload = "<script>alert('xss')</script>";
        mockMvc.perform(post("/api/posts")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"" + payload + "\",\"content\":\"hello & goodbye\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("&lt;script&gt;alert(&#39;xss&#39;)&lt;/script&gt;"))
                .andExpect(jsonPath("$.content").value("hello &amp; goodbye"));
    }

    @Test
    void passwordHashIsNeverExposed() throws Exception {
        String username = "user_" + UUID.randomUUID().toString().substring(0, 8);
        String token = registerAndLogin(username, "StrongPass123");

        String body = mockMvc.perform(get("/api/data")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertThat(body).doesNotContain("password").doesNotContain("$2a$").doesNotContain("$2b$");
    }
}
