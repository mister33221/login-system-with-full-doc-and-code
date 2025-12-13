package com.example.login.api.dto;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuditResponse {
    private UUID id;
    private OffsetDateTime createdAt;
    private String username;
    private String resource;
    private String action;
    private String decision;
    private String reason;
    private String ip;
    private String userAgent;
}
