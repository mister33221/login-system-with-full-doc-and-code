package com.example.login.api.dto;

import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoleResponse {
    private UUID id;
    private String code;
    private String name;
    private String description;
    private Set<String> permissions;
}
