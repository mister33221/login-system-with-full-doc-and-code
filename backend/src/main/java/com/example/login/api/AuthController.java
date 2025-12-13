package com.example.login.api;

import com.example.login.api.dto.LoginRequest;
import com.example.login.api.dto.LoginResponse;
import com.example.login.api.dto.RefreshRequest;
import com.example.login.api.dto.RefreshResponse;
import com.example.login.api.dto.RegisterRequest;
import com.example.login.application.AuditService;
import com.example.login.application.AuthResult;
import com.example.login.application.AuthService;
import com.example.login.application.SessionService;
import com.example.login.domain.SessionToken;
import com.example.login.domain.User;
import com.example.login.infrastructure.exception.ApiError;
import com.example.login.infrastructure.repository.RoleRepository;
import com.example.login.infrastructure.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Validated
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtEncoder jwtEncoder;
    private final SessionService sessionService;
    private final AuditService auditService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Validated RegisterRequest request, HttpServletRequest servletRequest) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiError.of("USERNAME_EXISTS", "帳號已存在"));
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setStatus("active");
        user.setFailedAttempts(0);

        var roleCodes = request.getRoleCodes();
        if (roleCodes == null || roleCodes.isEmpty()) {
            roleRepository.findByCode("USER").ifPresent(user.getRoles()::add);
        } else {
            roleCodes.stream()
                    .map(roleRepository::findByCode)
                    .filter(java.util.Optional::isPresent)
                    .map(java.util.Optional::get)
                    .forEach(user.getRoles()::add);
        }

        user = userRepository.save(user);

        String ip = servletRequest.getRemoteAddr();
        String userAgent = servletRequest.getHeader("User-Agent");
        String deviceInfo = userAgent != null ? userAgent : "unknown";
        SessionToken session = sessionService.createSession(user, deviceInfo, ip, userAgent);
        String refreshToken = session.getRefreshTokenId().toString();
        String accessToken = encodeAccessToken(user);

        auditService.record(user, session, "auth", "register", "allow", null, ip, userAgent);
        return ResponseEntity.status(HttpStatus.CREATED).body(new LoginResponse(accessToken, refreshToken));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Validated LoginRequest request, HttpServletRequest servletRequest) {
        AuthResult result = authService.authenticate(request.getUsername(), request.getPassword());
        if (result.getStatus() == AuthResult.Status.LOCKED) {
            Map<String, Object> data = new HashMap<>();
            data.put("lockedUntil", result.getLockedUntil());
            return ResponseEntity.status(HttpStatus.LOCKED)
                    .body(ApiError.of("LOCKED", "帳號已鎖定", data));
        }
        if (result.getStatus() == AuthResult.Status.FAILED) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiError.of("INVALID_CREDENTIALS", "帳號或密碼錯誤"));
        }

        User user = result.getUser();
        String ip = servletRequest.getRemoteAddr();
        String userAgent = servletRequest.getHeader("User-Agent");
        String deviceInfo = userAgent != null ? userAgent : "unknown";
        SessionToken session = sessionService.createSession(user, deviceInfo, ip, userAgent);
        String refreshToken = session.getRefreshTokenId().toString();

        String accessToken = encodeAccessToken(user);

        auditService.record(user, session, "auth", "login", "allow", null, ip, userAgent);
        return ResponseEntity.ok(new LoginResponse(accessToken, refreshToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody @Validated RefreshRequest request, HttpServletRequest servletRequest) {
        String refreshToken = request.getRefreshToken();
        String ip = servletRequest.getRemoteAddr();
        String userAgent = servletRequest.getHeader("User-Agent");

        var sessionOpt = sessionService.findValidSessionByRefreshToken(refreshToken);
        if (sessionOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiError.of("REFRESH_INVALID", "refresh token 無效或已過期/撤銷"));
        }

        var session = sessionOpt.get();
        String accessToken = encodeAccessToken(session.getUser());
        auditService.record(session.getUser(), session, "auth", "refresh", "allow", null, ip, userAgent);
        return ResponseEntity.ok(new RefreshResponse(accessToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody(required = false) Map<String, String> payload, HttpServletRequest servletRequest) {
        String refreshToken = payload != null ? payload.get("refreshToken") : null;
        boolean revoked = refreshToken != null && sessionService.revokeByRefreshToken(refreshToken);
        String ip = servletRequest.getRemoteAddr();
        String userAgent = servletRequest.getHeader("User-Agent");
        auditService.record(null, null, "auth", "logout", "allow", revoked ? "revoked" : "no-token", ip, userAgent);
        Map<String, Object> data = new HashMap<>();
        data.put("revoked", revoked);
        return ResponseEntity.ok(ApiError.of("LOGOUT_OK", "登出完成", data));
    }

    private String encodeAccessToken(User user) {
        Instant now = Instant.now();
        Set<String> roles = user.getRoles().stream().map(r -> r.getCode()).collect(Collectors.toSet());
        Set<String> perms = user.getRoles().stream()
                .flatMap(r -> r.getPermissions().stream())
                .map(p -> p.getCode())
                .collect(Collectors.toSet());

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("login-system")
                .issuedAt(now)
                .expiresAt(now.plus(15, ChronoUnit.MINUTES))
                .subject(user.getUsername())
                .claim("roles", new java.util.ArrayList<>(roles))
                .claim("perms", new java.util.ArrayList<>(perms))
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}
