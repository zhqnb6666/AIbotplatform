package com.aibotplatform.model;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "bots")
public class Bot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long botId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = true)
    private User creator; // Foreign key

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BotType type;

    @Column(nullable = false, length = 50)
    private String model;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false)
    private Integer tokenCost = 1;

    @Column(nullable = false)
    private Integer daily_limit = 50;

    @Column(nullable = false)
    private Timestamp createdAt;

    @Column(nullable = false)
    private Timestamp updatedAt;

    // Getters, Setters, equals, hashCode, etc.

    public Long getBotId() {
        return botId;
    }

    public void setBotId(Long botId) {
        this.botId = botId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public BotType getType() {
        return type;
    }

    public void setType(BotType type) {
        this.type = type;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Integer getTokenCost() {
        return tokenCost;
    }

    public void setTokenCost(Integer tokenCost) {
        this.tokenCost = tokenCost;
    }

    public Integer getDaily_limit() {
        return daily_limit;
    }

    public void setDaily_limit(Integer daily_limit) {
        this.daily_limit = daily_limit;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public enum BotType {
        OFFICIAL, CUSTOM
    }

}
