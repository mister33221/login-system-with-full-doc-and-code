package com.example.login.application;

import com.example.login.domain.Permission;
import com.example.login.domain.Role;
import com.example.login.domain.User;
import com.example.login.infrastructure.repository.PermissionRepository;
import com.example.login.infrastructure.repository.RoleRepository;
import com.example.login.infrastructure.repository.UserRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<Role> listRoles() {
        return roleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Permission> listPermissions() {
        return permissionRepository.findAll();
    }

    @Transactional
    public Role createRole(String code, String name, String description, Set<UUID> permissionIds) {
        Role role = new Role();
        role.setCode(code);
        role.setName(name);
        role.setDescription(description);
        role.setPermissions(new HashSet<>(loadPermissions(permissionIds)));
        return roleRepository.save(role);
    }

    @Transactional
    public Role updateRole(UUID id, String name, String description, Set<UUID> permissionIds) {
        Role role = roleRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));
        role.setName(name);
        role.setDescription(description);
        role.setPermissions(new HashSet<>(loadPermissions(permissionIds)));
        return roleRepository.save(role);
    }

    @Transactional
    public void deleteRole(UUID id) {
        roleRepository.deleteById(id);
    }

    @Transactional
    public User setUserRoles(String username, Set<UUID> roleIds) {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Set<Role> roles = new HashSet<>(roleRepository.findAllById(roleIds));
        user.setRoles(roles);
        return userRepository.save(user);
    }

    private List<Permission> loadPermissions(Set<UUID> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            return List.of();
        }
        return permissionRepository.findAllById(permissionIds);
    }
}
