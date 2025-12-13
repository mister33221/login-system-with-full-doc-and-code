package com.example.login.api.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PermissionResponse {
    private UUID id;
    private String code;
    private String resource;
    private String action;
    private String description;
}
