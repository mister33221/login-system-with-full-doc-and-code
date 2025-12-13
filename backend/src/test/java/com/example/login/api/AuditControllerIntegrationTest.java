package com.example.login.api;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.login.application.AuditService;
import com.example.login.domain.User;
import com.example.login.infrastructure.repository.UserRepository;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuditControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuditService auditService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    }

    @Test
    void shouldFilterByUser() throws Exception {
        User user = new User();
        user.setUsername("audituser");
        user.setPasswordHash("x");
        user.setStatus("active");
        userRepository.save(user);

        auditService.record(user, null, "auth", "login", "allow", null, "1.1.1.1", "test-agent");
        auditService.record(null, null, "role", "update", "deny", "no-role", "1.1.1.2", "test-agent");

        mockMvc.perform(get("/api/audit")
                        .with(jwt().authorities(() -> "audit:view"))
                        .param("user", "audituser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("audituser"))
                .andExpect(jsonPath("$[0].resource").value("auth"));
    }

    @Test
    void shouldAllowDateFilter() throws Exception {
        User user = new User();
        user.setUsername("bob");
        user.setPasswordHash("x");
        user.setStatus("active");
        userRepository.save(user);

        auditService.record(user, null, "auth", "login", "allow", null, "1.1.1.1", "test-agent");

        mockMvc.perform(get("/api/audit")
                        .with(jwt().authorities(() -> "audit:view"))
                        .param("from", OffsetDateTime.now().minusHours(1).toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].resource").value("auth"));
    }
}
