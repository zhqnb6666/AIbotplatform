package com.aibotplatform.model;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "bot_knowledge_base")
public class BotKnowledgeBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long kbId;

    @ManyToOne
    @JoinColumn(name = "bot_id", nullable = false)
    private Bot bot;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Timestamp createdAt;

    @Column(nullable = false)
    private Timestamp updatedAt;

    // Getters and Setters
}
