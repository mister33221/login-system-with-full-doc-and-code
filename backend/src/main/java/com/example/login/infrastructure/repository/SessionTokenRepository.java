package com.example.login.infrastructure.repository;

import com.example.login.domain.SessionToken;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionTokenRepository extends JpaRepository<SessionToken, UUID> {
    Optional<SessionToken> findByRefreshTokenId(UUID refreshTokenId);
}
