package com.example.login.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "permissions")
@Getter
@Setter
public class Permission {

    // UUID 主鍵，使用 Hibernate uuid2 產生器
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private UUID id;

    // 權限代碼，唯一
    @Column(nullable = false, unique = true, length = 128)
    private String code;

    // 對應的資源
    @Column(nullable = false, length = 128)
    private String resource;

    // 對應的動作
    @Column(nullable = false, length = 64)
    private String action;

    // 說明文字
    @Column(length = 255)
    private String description;
}
