package com.example.login.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "audit_events")
@Getter
@Setter
public class AuditEvent {

    // UUID 主鍵，使用 Hibernate uuid2 產生器
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private UUID id;

    // 發出事件的使用者與會話（可能為 null，例如匿名嘗試）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private SessionToken session;

    // 資源/動作與決策
    @Column(nullable = false, length = 128)
    private String resource;

    @Column(nullable = false, length = 64)
    private String action;

    // allow / deny
    @Column(nullable = false, length = 16)
    private String decision; // allow/deny

    @Column(length = 255)
    private String reason;

    // 來源 IP
    @Column(columnDefinition = "inet")
    private String ip;

    @Column(length = 255)
    private String userAgent;

    // 事件時間
    @Column(nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();
}
