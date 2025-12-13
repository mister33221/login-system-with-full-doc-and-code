package com.example.login.api.dto;

import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleRequest {
    private String code;
    private String name;
    private String description;
    private Set<UUID> permissionIds;
}
