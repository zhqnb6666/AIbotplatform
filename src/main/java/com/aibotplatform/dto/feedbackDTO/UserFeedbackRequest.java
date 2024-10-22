package com.aibotplatform.dto.feedbackDTO;

public record UserFeedbackRequest(
        Long userId,
        String content,
        Integer rating
) {
    public UserFeedbackRequest {
        if (rating < 0 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 0 and 5");
        }
    }
}
