package com.aibotplatform.dto;

import com.aibotplatform.model.MessageFeedback;

public record MessageFeedbackDTO(Long messageFeedbackId,
                                 Long messageId,
                                 Long CommenterId,
                                 String content,
                                 MessageFeedback.FeedbackType type) {
}
