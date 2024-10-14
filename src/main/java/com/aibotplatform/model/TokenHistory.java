package com.aibotplatform.model;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "token_history")
public class TokenHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tokenHistoryId;

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @Column(nullable = false)
    private Long tokenChange;

    @Column(nullable = false)
    private Long tokenBalance;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Timestamp createdAt;

    public TokenHistory() {
    }

    public TokenHistory(Long tokenHistoryId, User user, Long tokenChange, Long tokenBalance, String description) {
        this.tokenHistoryId = tokenHistoryId;
        this.user = user;
        this.tokenChange = tokenChange;
        this.tokenBalance = tokenBalance;
        this.description = description;
    }

    public Long getTokenHistoryId() {
        return tokenHistoryId;
    }

    public void setTokenHistoryId(Long tokenHistoryId) {
        this.tokenHistoryId = tokenHistoryId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getTokenChange() {
        return tokenChange;
    }

    public void setTokenChange(Long tokenChange) {
        this.tokenChange = tokenChange;
    }

    public Long getTokenBalance() {
        return tokenBalance;
    }

    public void setTokenBalance(Long tokenBalance) {
        this.tokenBalance = tokenBalance;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
