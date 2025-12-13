package com.example.login.application;

import com.example.login.domain.SessionToken;
import com.example.login.domain.User;
import com.example.login.infrastructure.repository.SessionTokenRepository;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionTokenRepository sessionTokenRepository;

    @Transactional
    public SessionToken createSession(User user, String deviceInfo, String ip, String userAgent) {
        SessionToken token = new SessionToken();
        token.setUser(user);
        token.setRefreshTokenId(UUID.randomUUID());
        token.setDeviceInfo(deviceInfo);
        token.setIp(ip);
        token.setUserAgent(userAgent);
        token.setIssuedAt(OffsetDateTime.now());
        token.setExpiresAt(OffsetDateTime.now().plus(12, ChronoUnit.HOURS));
        sessionTokenRepository.save(token);
        return token;
    }

    @Transactional
    public boolean revokeByRefreshToken(String refreshToken) {
        try {
            UUID id = UUID.fromString(refreshToken);
            Optional<SessionToken> opt = sessionTokenRepository.findByRefreshTokenId(id);
            if (opt.isPresent()) {
                SessionToken token = opt.get();
                token.setRevokedAt(OffsetDateTime.now());
                token.setRevokedReason("manual-logout");
                sessionTokenRepository.save(token);
                return true;
            }
        } catch (IllegalArgumentException ignored) {
            // invalid UUID format
        }
        return false;
    }

    public Optional<SessionToken> findValidSessionByRefreshToken(String refreshToken) {
        try {
            UUID id = UUID.fromString(refreshToken);
            Optional<SessionToken> opt = sessionTokenRepository.findByRefreshTokenId(id);
            if (opt.isPresent()) {
                SessionToken token = opt.get();
                boolean expired = token.getExpiresAt() != null && token.getExpiresAt().isBefore(OffsetDateTime.now());
                boolean revoked = token.getRevokedAt() != null;
                if (!expired && !revoked) {
                    return opt;
                }
            }
        } catch (IllegalArgumentException ignored) {
            // invalid UUID format
        }
        return Optional.empty();
    }
}
