package com.example.login.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.example.login.domain.User;
import com.example.login.infrastructure.repository.UserRepository;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User activeUser;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        activeUser = new User();
        activeUser.setUsername("john");
        activeUser.setPasswordHash("hashed");
        activeUser.setStatus("active");
        activeUser.setFailedAttempts(0);
    }

    @Test
    void authenticate_success_resets_failed_attempts() {
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches("pw", "hashed")).thenReturn(true);

        AuthResult result = authService.authenticate("john", "pw");

        assertThat(result.getStatus()).isEqualTo(AuthResult.Status.SUCCESS);
        assertThat(result.getLockedUntil()).isNull();
        assertThat(result.getUser()).isNotNull();
    }

    @Test
    void authenticate_lock_after_threshold() {
        activeUser.setFailedAttempts(4);
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        AuthResult result = authService.authenticate("john", "wrong");

        assertThat(result.getStatus()).isEqualTo(AuthResult.Status.LOCKED);
        assertThat(activeUser.getStatus()).isEqualTo("locked");
        assertThat(activeUser.getLockedUntil()).isNotNull();
    }

    @Test
    void authenticate_locked_user_returns_locked() {
        activeUser.setStatus("locked");
        activeUser.setLockedUntil(OffsetDateTime.now().plusMinutes(10));
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(activeUser));

        AuthResult result = authService.authenticate("john", "pw");

        assertThat(result.getStatus()).isEqualTo(AuthResult.Status.LOCKED);
        assertThat(result.getLockedUntil()).isNotNull();
    }
}
