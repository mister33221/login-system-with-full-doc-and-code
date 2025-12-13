package com.example.login.api;

import com.example.login.api.dto.LoginRequest;
import com.example.login.api.dto.LoginResponse;
import com.example.login.api.dto.RefreshRequest;
import com.example.login.api.dto.RefreshResponse;
import com.example.login.api.dto.RegisterRequest;
import com.example.login.application.AuthResult;
import com.example.login.application.AuthService;
import com.example.login.application.AuditService;
import com.example.login.application.SessionService;
import com.example.login.domain.SessionToken;
import com.example.login.domain.User;
import com.example.login.infrastructure.repository.UserRepository;
import com.example.login.infrastructure.repository.RoleRepository;
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
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

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
        // 檢查用戶名是否已存在
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            Map<String, Object> body = new HashMap<>();
            body.put("code", "USERNAME_EXISTS");
            body.put("message", "帳號已存在");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
        }

        // 創建新用戶
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setStatus("active");
        user.setFailedAttempts(0);
        
        // 分配 USER 角色
        var userRole = roleRepository.findByCode("USER");
        if (userRole.isPresent()) {
            user.getRoles().add(userRole.get());
        }
        
        user = userRepository.save(user);

        // 註冊成功後自動登入，創建 session 並返回 token
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
            Map<String, Object> body = new HashMap<>();
            body.put("code", "LOCKED");
            body.put("message", "帳號已鎖定");
            body.put("lockedUntil", result.getLockedUntil());
            return ResponseEntity.status(HttpStatus.LOCKED).body(body);
        }
        if (result.getStatus() == AuthResult.Status.FAILED) {
            Map<String, Object> body = new HashMap<>();
            body.put("code", "INVALID_CREDENTIALS");
            body.put("message", "帳號或密碼錯誤");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
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
            Map<String, Object> body = new HashMap<>();
            body.put("code", "REFRESH_INVALID");
            body.put("message", "refresh token 無效或已過期/撤銷");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
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
        Map<String, Object> body = new HashMap<>();
        body.put("success", true);
        body.put("revoked", revoked);
        return ResponseEntity.ok(body);
    }

    private String encodeAccessToken(User user) {
        try {
            Instant now = Instant.now();
            Set<String> roles = user.getRoles().stream().map(r -> r.getCode()).collect(Collectors.toSet());
            Set<String> perms = user.getRoles().stream()
                    .flatMap(r -> r.getPermissions().stream())
                    .map(p -> p.getCode())
                    .collect(Collectors.toSet());

            System.out.println("=== Creating JWT Claims ===");
            System.out.println("Username: " + user.getUsername());
            System.out.println("Roles: " + roles);
            System.out.println("Permissions: " + perms);

            JwtClaimsSet claims = JwtClaimsSet.builder()
                    .issuer("login-system")
                    .issuedAt(now)
                    .expiresAt(now.plus(15, ChronoUnit.MINUTES))
                    .subject(user.getUsername())
                    .claim("roles", new java.util.ArrayList<>(roles))
                    .claim("perms", new java.util.ArrayList<>(perms))
                    .build();
            
            System.out.println("Claims created, encoding...");
            var header = org.springframework.security.oauth2.jwt.JwsHeader.with(MacAlgorithm.HS256).build();
            String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
            System.out.println("Token encoded successfully, length: " + token.length());
            return token;
        } catch (Exception e) {
            System.err.println("Error encoding JWT: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
