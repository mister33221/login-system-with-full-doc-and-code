package com.example.login.config;

import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final SecurityProperties securityProperties;

    public SecurityConfig(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/health", "/api/auth/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));
        return http.build();
    }

    @Bean
    JwtDecoder jwtDecoder() {
        String secret = securityProperties.getSecret();
        byte[] keyBytes = java.util.Base64.getDecoder().decode(secret);
        SecretKey key = new SecretKeySpec(keyBytes, "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(key).build();
    }

    @Bean
    JwtEncoder jwtEncoder() {
        String secret = securityProperties.getSecret();
        System.out.println("=== JWT Encoder Configuration ===");
        System.out.println("Secret length: " + secret.length());
        System.out.println("Secret starts with: " + secret.substring(0, Math.min(10, secret.length())));
        
        byte[] keyBytes;
        try {
            keyBytes = java.util.Base64.getDecoder().decode(secret);
            System.out.println("Decoded key bytes length: " + keyBytes.length);
        } catch (Exception e) {
            System.err.println("Failed to decode Base64 secret, using raw bytes");
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
            if (keyBytes.length < 32) {
                byte[] padded = new byte[32];
                System.arraycopy(keyBytes, 0, padded, 0, keyBytes.length);
                keyBytes = padded;
            }
            System.out.println("Raw key bytes length: " + keyBytes.length);
        }
        
        try {
            OctetSequenceKey jwk = new OctetSequenceKey.Builder(keyBytes)
                    .keyID("login-system-key")
                    .algorithm(com.nimbusds.jose.JWSAlgorithm.HS256)
                    .keyUse(com.nimbusds.jose.jwk.KeyUse.SIGNATURE)
                    .build();
            System.out.println("JWK created successfully");
            System.out.println("JWK Algorithm: " + jwk.getAlgorithm());
            System.out.println("JWK Key ID: " + jwk.getKeyID());
            System.out.println("JWK Key Use: " + jwk.getKeyUse());
            
            JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new com.nimbusds.jose.jwk.JWKSet(jwk));
            System.out.println("JWKSource created successfully");
            return new NimbusJwtEncoder(jwkSource);
        } catch (Exception e) {
            System.err.println("Error creating JWT encoder: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to create JWT encoder", e);
        }
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Collection<SimpleGrantedAuthority> roleAuthorities = jwt.getClaimAsStringList("roles") == null
                    ? java.util.List.of()
                    : jwt.getClaimAsStringList("roles").stream()
                            .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                            .toList();
            Collection<SimpleGrantedAuthority> permAuthorities = jwt.getClaimAsStringList("perms") == null
                    ? java.util.List.of()
                    : jwt.getClaimAsStringList("perms").stream()
                            .map(SimpleGrantedAuthority::new)
                            .toList();
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            authorities.addAll(roleAuthorities);
            authorities.addAll(permAuthorities);
            return authorities;
        });
        return converter;
    }
}
