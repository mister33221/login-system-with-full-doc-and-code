package com.example.login.api;

import com.example.login.api.dto.PermissionResponse;
import com.example.login.api.dto.RoleRequest;
import com.example.login.api.dto.RoleResponse;
import com.example.login.application.RoleService;
import com.example.login.domain.Permission;
import com.example.login.domain.Role;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated
public class RoleController {

    private final RoleService roleService;

    @GetMapping("/permissions")
    @PreAuthorize("hasAuthority('permission:view') or hasAuthority('role:view')")
    public List<PermissionResponse> listPermissions() {
        return roleService.listPermissions().stream()
                .map(this::toPermissionResponse)
                .toList();
    }

    @GetMapping("/roles")
    @PreAuthorize("hasAuthority('role:view')")
    public List<RoleResponse> listRoles() {
        return roleService.listRoles().stream()
                .map(this::toRoleResponse)
                .toList();
    }

    @PostMapping("/roles")
    @PreAuthorize("hasAuthority('role:create')")
    public ResponseEntity<RoleResponse> createRole(@RequestBody @Validated RoleRequest request) {
        Role created = roleService.createRole(
                request.getCode(), request.getName(), request.getDescription(), request.getPermissionIds());
        return ResponseEntity.ok(toRoleResponse(created));
    }

    @PutMapping("/roles/{id}")
    @PreAuthorize("hasAuthority('role:update')")
    public ResponseEntity<RoleResponse> updateRole(
            @PathVariable UUID id, @RequestBody @Validated RoleRequest request) {
        Role updated = roleService.updateRole(
                id, request.getName(), request.getDescription(), request.getPermissionIds());
        return ResponseEntity.ok(toRoleResponse(updated));
    }

    @DeleteMapping("/roles/{id}")
    @PreAuthorize("hasAuthority('role:delete')")
    public ResponseEntity<Void> deleteRole(@PathVariable UUID id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }

    private PermissionResponse toPermissionResponse(Permission p) {
        return new PermissionResponse(p.getId(), p.getCode(), p.getResource(), p.getAction(), p.getDescription());
    }

    private RoleResponse toRoleResponse(Role r) {
        Set<String> perms = r.getPermissions().stream()
                .map(Permission::getCode)
                .collect(Collectors.toSet());
        return new RoleResponse(r.getId(), r.getCode(), r.getName(), r.getDescription(), perms);
    }
}
