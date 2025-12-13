package com.example.login.api;

import com.example.login.api.dto.CurrentUserResponse;
import com.example.login.api.dto.UserRolesRequest;
import com.example.login.application.RoleService;
import com.example.login.domain.Permission;
import com.example.login.domain.User;
import com.example.login.infrastructure.repository.UserRepository;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final RoleService roleService;
    private final UserRepository userRepository;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CurrentUserResponse> currentUser(@AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getSubject();
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Set<String> roles = user.getRoles().stream().map(r -> r.getCode()).collect(Collectors.toSet());
        Set<String> perms = user.getRoles().stream()
                .flatMap(r -> r.getPermissions().stream())
                .map(Permission::getCode)
                .collect(Collectors.toSet());
        return ResponseEntity.ok(new CurrentUserResponse(username, user.getEmail(), roles, perms));
    }

    @PutMapping("/users/{username}/roles")
    @PreAuthorize("hasAuthority('user:assign-role')")
    public ResponseEntity<Void> assignRoles(
            @PathVariable String username, @RequestBody @Validated UserRolesRequest request) {
        roleService.setUserRoles(username, request.getRoleIds());
        return ResponseEntity.noContent().build();
    }
}
