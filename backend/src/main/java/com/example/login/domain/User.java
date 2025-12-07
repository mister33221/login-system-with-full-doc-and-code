package com.example.login.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    // UUID 主鍵，使用 Hibernate uuid2 產生器
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private UUID id;

    // 使用者帳號，唯一
    @Column(nullable = false, unique = true, length = 64)
    private String username;

    // 雜湊後密碼
    @Column(nullable = false, length = 255)
    private String passwordHash;

    @Column(unique = true, length = 128)
    private String email;

    // active / locked / suspended
    @Column(nullable = false, length = 16)
    private String status; // active, locked, suspended

    // 連續失敗次數
    @Column(nullable = false)
    private int failedAttempts = 0;

    // 鎖定截止時間
    private OffsetDateTime lockedUntil;

    // 建立與更新時間
    @Column(nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    // 使用者所屬的角色，以中介表 user_roles 維護。
    // ManyToMany: 多對多關聯；fetch = EAGER 代表載入 User 時同步載入 Roles（如需減少載入，可改用 LAZY 搭配 join fetch）。
    // JoinTable: 指定中介表與外鍵欄位名稱（user_id -> User, role_id -> Role）。
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();
}
