package com.aibotplatform.dto.feedbackDTO;

public record BotRatingDetailResponse(
        String username,
        String avatarUrl,
        Integer rating
) {
}
