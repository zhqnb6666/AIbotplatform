package com.aibotplatform.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "credit_history")
public class CreditHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long creditHistoryId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private BigDecimal creditChange;

    @Column(nullable = false)
    private BigDecimal creditBalance;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Timestamp createdAt;

    public CreditHistory() {
    }

    public CreditHistory(Long creditHistoryId, User user, BigDecimal creditChange, BigDecimal creditBalance, String description, Timestamp createdAt) {
        this.creditHistoryId = creditHistoryId;
        this.user = user;
        this.creditChange = creditChange;
        this.creditBalance = creditBalance;
        this.description = description;
        this.createdAt = createdAt;
    }

    public Long getCreditHistoryId() {
        return creditHistoryId;
    }

    public void setCreditHistoryId(Long creditHistoryId) {
        this.creditHistoryId = creditHistoryId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BigDecimal getCreditChange() {
        return creditChange;
    }

    public void setCreditChange(BigDecimal creditChange) {
        this.creditChange = creditChange;
    }

    public BigDecimal getCreditBalance() {
        return creditBalance;
    }

    public void setCreditBalance(BigDecimal creditBalance) {
        this.creditBalance = creditBalance;
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
