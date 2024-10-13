package com.aibotplatform.dto;

import java.sql.Timestamp;

public class FeedbackDTO {
    private String comment;
    private Timestamp createdAt;

    public FeedbackDTO(String comment, Timestamp createdAt) {
        this.comment = comment;
        this.createdAt = createdAt;
    }

    // Getters and setters

    public String getComment() {
        return comment;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }
}