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
    private Timestamp createdAt;

    @Column(nullable = false)
    private Timestamp updatedAt;

    // Getters, Setters, equals, hashCode, etc.

    public enum BotType {
        OFFICIAL, CUSTOM
    }

}
