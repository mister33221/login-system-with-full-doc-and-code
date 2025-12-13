package com.example.login.api;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.login.domain.Permission;
import com.example.login.domain.Role;
import com.example.login.domain.User;
import com.example.login.infrastructure.repository.PermissionRepository;
import com.example.login.infrastructure.repository.RoleRepository;
import com.example.login.infrastructure.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RoleControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    private Permission pCreate;
    private Permission pView;

    @BeforeEach
    void setup() {
        roleRepository.deleteAll();
        permissionRepository.deleteAll();
        userRepository.deleteAll();
        pCreate = permissionRepository.save(buildPermission("role:create", "role", "create"));
        pView = permissionRepository.save(buildPermission("role:view", "role", "view"));
        permissionRepository.save(buildPermission("user:assign-role", "user", "assign-role"));
    }

    @Test
    void shouldCreateAndListRole() throws Exception {
        String payload = objectMapper.writeValueAsString(new RolePayload("ADMIN2", "Admin 2", Set.of(pCreate.getId())));

        mockMvc.perform(post("/api/roles")
                        .with(jwt().authorities(() -> "role:create"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("ADMIN2"));

        mockMvc.perform(get("/api/roles").with(jwt().authorities(() -> "role:view")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").exists());
    }

    @Test
    void shouldAssignRolesToUser() throws Exception {
        Role role = roleRepository.save(buildRole("EDITOR", "Editor", Set.of(pView)));
        User user = new User();
        user.setUsername("bob");
        user.setPasswordHash("x");
        user.setStatus("active");
        userRepository.save(user);

        String payload = """
                {"roleIds":["%s"]}
                """.formatted(role.getId());

        mockMvc.perform(put("/api/users/bob/roles")
                        .with(jwt().authorities(() -> "user:assign-role"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldDeleteRole() throws Exception {
        Role role = roleRepository.save(buildRole("TEMP", "Temp", Set.of(pCreate)));

        mockMvc.perform(delete("/api/roles/" + role.getId())
                        .with(jwt().authorities(() -> "role:delete")))
                .andExpect(status().isNoContent());
    }

    private Permission buildPermission(String code, String resource, String action) {
        Permission p = new Permission();
        p.setCode(code);
        p.setResource(resource);
        p.setAction(action);
        return p;
    }

    private Role buildRole(String code, String name, Set<Permission> perms) {
        Role r = new Role();
        r.setCode(code);
        r.setName(name);
        r.setDescription(name);
        r.setPermissions(perms);
        return r;
    }

    record RolePayload(String code, String name, Set<java.util.UUID> permissionIds) {}
}
