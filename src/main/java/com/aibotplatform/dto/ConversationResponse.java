package com.aibotplatform.dto;

public record ConversationResponse(
        Long botId,
        String botName,
        Long messageId
) {
}
