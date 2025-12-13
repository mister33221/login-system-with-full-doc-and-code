package com.example.login.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "sessions")
@Getter
@Setter
public class SessionToken {

    // UUID 主鍵，使用 Hibernate uuid2 產生器
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private UUID id;

    // 所屬使用者；ManyToOne + LAZY 避免未使用時過度載入
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    // 對應 refresh token 的唯一識別
    @Column(nullable = false, unique = true)
    private UUID refreshTokenId;

    @Column(length = 255)
    private String deviceInfo;

    // 來源 IP
    @Column(length = 45)
    private String ip;

    @Column(length = 255)
    private String userAgent;

    // 發行與到期時間
    @Column(nullable = false)
    private OffsetDateTime issuedAt;

    @Column(nullable = false)
    private OffsetDateTime expiresAt;

    // 撤銷資訊
    private OffsetDateTime revokedAt;

    @Column(length = 128)
    private String revokedReason;
}
