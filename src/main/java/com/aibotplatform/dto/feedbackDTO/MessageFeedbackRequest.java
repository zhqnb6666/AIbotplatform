package com.aibotplatform.dto.feedbackDTO;

import com.aibotplatform.model.MessageFeedback;

public record MessageFeedbackRequest (
        Long messageId,
        String content,
        MessageFeedback.FeedbackType type
) {
}
