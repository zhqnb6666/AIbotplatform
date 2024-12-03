package com.aibotplatform.dto.feedbackDTO;

public record BotRatingResponse(
        Long botId,
        Double averageRating,
        Double oneStarPercentage,
        Double twoStarPercentage,
        Double threeStarPercentage,
        Double fourStarPercentage,
        Double fiveStarPercentage
) {
}
