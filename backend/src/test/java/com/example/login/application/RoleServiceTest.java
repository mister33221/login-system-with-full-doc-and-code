package com.example.login.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.login.domain.Permission;
import com.example.login.domain.Role;
import com.example.login.domain.User;
import com.example.login.infrastructure.repository.PermissionRepository;
import com.example.login.infrastructure.repository.RoleRepository;
import com.example.login.infrastructure.repository.UserRepository;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(RoleService.class)
class RoleServiceTest {

    @Autowired
    private RoleService roleService;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    private Permission pView;
    private Permission pCreate;

    @BeforeEach
    void setUp() {
        pView = permissionRepository.save(buildPermission("role:view", "role", "view"));
        pCreate = permissionRepository.save(buildPermission("role:create", "role", "create"));
    }

    @Test
    void createRoleShouldPersistPermissions() {
        Role role = roleService.createRole(
                "MANAGER", "Manager", "Manage stuff", Set.of(pView.getId(), pCreate.getId()));

        Role found = roleRepository.findById(role.getId()).orElseThrow();
        assertThat(found.getPermissions()).extracting("code").containsExactlyInAnyOrder("role:view", "role:create");
    }

    @Test
    void setUserRolesShouldReplaceRoles() {
        Role r1 = roleService.createRole("R1", "Role1", null, Set.of(pView.getId()));
        Role r2 = roleService.createRole("R2", "Role2", null, Set.of(pCreate.getId()));
        User user = new User();
        user.setUsername("alice");
        user.setPasswordHash("x");
        user.setStatus("active");
        userRepository.save(user);

        roleService.setUserRoles("alice", Set.of(r1.getId(), r2.getId()));

        User reloaded = userRepository.findByUsername("alice").orElseThrow();
        assertThat(reloaded.getRoles()).hasSize(2);
    }

    private Permission buildPermission(String code, String resource, String action) {
        Permission p = new Permission();
        p.setCode(code);
        p.setResource(resource);
        p.setAction(action);
        return p;
    }
}
