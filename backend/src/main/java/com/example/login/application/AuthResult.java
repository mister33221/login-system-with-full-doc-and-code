package com.example.login.application;

import java.time.OffsetDateTime;
import lombok.Builder;
import lombok.Getter;
import com.example.login.domain.User;

@Getter
@Builder
public class AuthResult {
    public enum Status {
        SUCCESS,
        FAILED,
        LOCKED
    }

    private final Status status;
    private final String message;
    private final OffsetDateTime lockedUntil;
    private final User user;
}
