package com.example.login.api.dto;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CurrentUserResponse {
    private String username;
    private String email;
    private Set<String> roles;
    private Set<String> permissions;
}
