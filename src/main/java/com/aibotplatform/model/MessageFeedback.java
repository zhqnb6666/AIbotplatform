package com.aibotplatform.model;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "message_feedback")
public class MessageFeedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageFeedbackId;

    @Column
    private String content;

    @ManyToOne
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    @ManyToOne
    @JoinColumn(name = "commenter_id", nullable = false)
    private User commenter;

    @Column
    private Timestamp createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeedbackType feedbackType;

    public enum FeedbackType {
        LIKE,
        DISLIKE
    }

    public MessageFeedback(long messageFeedbackId, String content, Message message, User commenter, FeedbackType feedbackType) {
        this.messageFeedbackId = messageFeedbackId;
        this.content = content;
        this.commenter = commenter;
        this.message = message;
        this.feedbackType = feedbackType;
    }

    public MessageFeedback() {
    }

    public long getMessageFeedbackId() {
        return messageFeedbackId;
    }

    public void setMessageFeedbackId(Long messageFeedbackId) {
        this.messageFeedbackId = messageFeedbackId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public User getCommenter() {
        return commenter;
    }

    public void setCommenter(User commenter) {
        this.commenter = commenter;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public FeedbackType getFeedbackType() {
        return feedbackType;
    }

    public void setFeedbackType(FeedbackType feedbackType) {
        this.feedbackType = feedbackType;
    }
}
