package com.example.login.application;

import com.example.login.domain.User;
import com.example.login.infrastructure.repository.UserRepository;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 鎖定策略：5 次失敗鎖 15 分鐘
    private static final int LOCK_THRESHOLD = 5;
    private static final int LOCK_MINUTES = 15;

    @Transactional
    public AuthResult authenticate(String username, String rawPassword) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return AuthResult.builder()
                    .status(AuthResult.Status.FAILED)
                    .message("帳號或密碼錯誤")
                    .build();
        }

        User user = userOpt.get();
        if ("locked".equalsIgnoreCase(user.getStatus())) {
            return AuthResult.builder()
                    .status(AuthResult.Status.LOCKED)
                    .message("帳號已鎖定")
                    .lockedUntil(user.getLockedUntil())
                    .build();
        }

        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            int attempts = user.getFailedAttempts() + 1;
            user.setFailedAttempts(attempts);
            if (attempts >= LOCK_THRESHOLD) {
                OffsetDateTime lockedUntil = OffsetDateTime.now().plus(LOCK_MINUTES, ChronoUnit.MINUTES);
                user.setStatus("locked");
                user.setLockedUntil(lockedUntil);
                userRepository.save(user);
                return AuthResult.builder()
                        .status(AuthResult.Status.LOCKED)
                        .message("帳號已鎖定")
                        .lockedUntil(lockedUntil)
                        .build();
            }
            userRepository.save(user);
            return AuthResult.builder()
                    .status(AuthResult.Status.FAILED)
                    .message("帳號或密碼錯誤")
                    .build();
        }

        // 成功：重置失敗次數與鎖定狀態
        user.setFailedAttempts(0);
        user.setLockedUntil(null);
        user.setStatus("active");
        userRepository.save(user);

        return AuthResult.builder()
                .status(AuthResult.Status.SUCCESS)
                .message("登入成功")
                .user(user)
                .build();
    }
}
