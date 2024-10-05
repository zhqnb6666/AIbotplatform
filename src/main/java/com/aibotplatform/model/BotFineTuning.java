package com.aibotplatform.model;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "bot_fine_tuning")
public class BotFineTuning {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tuningId;

    @ManyToOne
    @JoinColumn(name = "bot_id", nullable = false)
    private Bot bot;

    @Column(nullable = false, length = 255)
    private String datasetUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TuningStatus status;

    @Column(nullable = false)
    private Timestamp createdAt;

    @Column(nullable = false)
    private Timestamp updatedAt;

    // Getters and Setters

    public enum TuningStatus {
        PENDING, IN_PROGRESS, COMPLETED, FAILED
    }
}
