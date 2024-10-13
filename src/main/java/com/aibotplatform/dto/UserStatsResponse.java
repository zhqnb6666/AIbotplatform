// UserStatsDTO.java
package com.aibotplatform.dto;

public class UserStatsResponse {
    private int totalConversations;
    private int totalMessages;
    private int totalBots;

    // Constructors, getters and setters

    public UserStatsResponse(int totalConversations, int totalMessages, int totalBots) {
        this.totalConversations = totalConversations;
        this.totalMessages = totalMessages;
        this.totalBots = totalBots;
    }

    public int getTotalConversations() {
        return totalConversations;
    }

    public void setTotalConversations(int totalConversations) {
        this.totalConversations = totalConversations;
    }

    public int getTotalMessages() {
        return totalMessages;
    }

    public void setTotalMessages(int totalMessages) {
        this.totalMessages = totalMessages;
    }

    public int getTotalBots() {
        return totalBots;
    }

    public void setTotalBots(int totalBots) {
        this.totalBots = totalBots;
    }
}