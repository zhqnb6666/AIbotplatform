package com.aibotplatform.model;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @ManyToOne
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SenderType senderType;

    @ManyToOne
    @JoinColumn(name = "bot_id",nullable = true)
    private Bot bot;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Timestamp createdAt;

    // Getters and Setters

    public Message() {
    }

    public Message(Long messageId, Conversation conversation,Bot bot, SenderType senderType, String content, Timestamp createdAt) {
        this.messageId = messageId;
        this.conversation = conversation;
        this.bot = bot;
        this.senderType = senderType;
        this.content = content;
        this.createdAt = createdAt;
    }

    public Message(Conversation conversation,Bot bot, SenderType senderType, String content) {
        this.conversation = conversation;
        this.bot = bot;
        this.senderType = senderType;
        this.content = content;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    public Bot getBot() {
        return bot;
    }

    public void setBot(Bot bot) {
        this.bot = bot;
    }

    public SenderType getSenderType() {
        return senderType;
    }

    public void setSenderType(SenderType senderType) {
        this.senderType = senderType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public enum SenderType {
        USER, BOT
    }
}