package com.aibotplatform.model;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "bot_usage_limits")
public class BotUsageLimit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long limitId;

    @ManyToOne
    @JoinColumn(name = "bot_id", nullable = false)
    private Bot bot;

    @Column(nullable = false)
    private Integer dailyLimit;

    @Column(nullable = false)
    private Integer monthlyLimit;

    @Column(nullable = false)
    private Timestamp createdAt;

    @Column(nullable = false)
    private Timestamp updatedAt;

    // Getters and Setters
}
