package com.aibotplatform.dto.feedbackDTO;

public record BotRatingRequest(
        Long botId,
        Integer rating
) {
    public BotRatingRequest {
        if (rating < 0 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 0 and 5");
        }
    }
}
