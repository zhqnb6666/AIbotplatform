package com.aibotplatform.dto;

public record BotRatingDTO(Long botId, Long userId, Integer rating) {
    public BotRatingDTO {
        if (rating < 0 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 0 and 5");
        }
    }
}