package com.example.login.domain;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "roles")
@Getter
@Setter
public class Role {

    // UUID 主鍵，使用 Hibernate uuid2 產生器（RFC 4122 variant 2，適合分散式唯一鍵）
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private UUID id;

    // 角色代碼，唯一
    @Column(nullable = false, unique = true, length = 64)
    private String code;

    // 角色名稱
    @Column(nullable = false, length = 128)
    private String name;

    // 角色描述
    @Column(length = 255)
    private String description;

    // 角色所擁有的權限，以中介表 role_permissions 維護。
    // ManyToMany: 多對多關聯；EAGER 載入角色時同步載入權限（需要時可改 LAZY）。
    // JoinTable: 指定中介表與外鍵欄位名稱（role_id -> Role, permission_id -> Permission）。
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "role_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<Permission> permissions = new HashSet<>();
}
