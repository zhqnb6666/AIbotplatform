package com.aibotplatform.dto;

import com.aibotplatform.model.User;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class UserDTO {
    private Long userId;
    private String username;
    private String email;
    private User.Role role;
    private BigDecimal credits;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public UserDTO(Long userId, String username, String email, User.Role role, BigDecimal credits, Timestamp createdAt, Timestamp updatedAt) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.role = role;
        this.credits = credits;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public User.Role getRole() {
        return role;
    }

    public BigDecimal getCredits() {
        return credits;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
// Constructors, getters and setters
}