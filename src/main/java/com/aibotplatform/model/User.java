package com.aibotplatform.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Data
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal credits = BigDecimal.ZERO;

    @Column(nullable = false)
    private Long token = 0L;

    @Column(nullable = false)
    private Timestamp createdAt;

    @Column(nullable = false)
    private Timestamp updatedAt;

    @Column(nullable = false, columnDefinition = "VARCHAR(255) DEFAULT 'default_avatar.png'")
    private String avatarUrl;

    @Column(columnDefinition = "TEXT")
    private String bio;

    public enum Role {
        ADMIN, USER
    }
}
